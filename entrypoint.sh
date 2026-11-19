#!/bin/sh
set -e

# If DATABASE_URL is provided (standard on Render for PostgreSQL)
if [ -n "$DATABASE_URL" ] && [ -z "$SPRING_DATASOURCE_URL" ]; then
    case "$DATABASE_URL" in
        postgres://*|postgresql://*)
            # Strip postgres:// or postgresql:// scheme
            CLEAN_URL=$(echo "$DATABASE_URL" | sed -E 's|^postgres(ql)?://||')
            
            if echo "$CLEAN_URL" | grep -q "@"; then
                USER_PASS="${CLEAN_URL%%@*}"
                HOST_AND_DB="${CLEAN_URL#*@}"

                DB_USER="${USER_PASS%%:*}"
                DB_PASS="${USER_PASS#*:}"

                # Ensure SSL if connecting to Render external domain and sslmode not yet set
                if echo "$HOST_AND_DB" | grep -q "\.render\.com" && ! echo "$HOST_AND_DB" | grep -q "sslmode="; then
                    if echo "$HOST_AND_DB" | grep -q "\?"; then
                        HOST_AND_DB="${HOST_AND_DB}&sslmode=require"
                    else
                        HOST_AND_DB="${HOST_AND_DB}?sslmode=require"
                    fi
                fi

                export SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-$DB_USER}"
                export SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-$DB_PASS}"
                export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOST_AND_DB}"
            else
                export SPRING_DATASOURCE_URL="jdbc:postgresql://${CLEAN_URL}"
            fi
            ;;
        jdbc:*)
            export SPRING_DATASOURCE_URL="$DATABASE_URL"
            ;;
        *)
            export SPRING_DATASOURCE_URL="jdbc:postgresql://${DATABASE_URL}"
            ;;
    esac
fi

# Set default active profile to prod if none provided
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"

# Default JVM options tuned for container environments
JAVA_OPTS="${JAVA_OPTS:--XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0}"

echo "Starting CitMobi on port ${PORT:-8080} (Profile: ${SPRING_PROFILES_ACTIVE})..."

exec java $JAVA_OPTS -Dserver.port="${PORT:-8080}" -jar app.war "$@"

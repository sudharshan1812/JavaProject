#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

case "${1:-run}" in
  build)
    mvn -f backend/pom.xml -q -DskipTests package
    ;;
  backend)
    exec java -jar backend/target/smart-toll-backend-1.0.0.jar
    ;;
  frontend)
    exec python3 -m http.server 5500 --directory frontend
    ;;
  *)
    echo "usage: ./run.sh {build|backend|frontend}"
    ;;
esac
#!/usr/bin/env bash
# =============================================================
# Deploy Grafana dashboards to bavanchun.grafana.net
# Usage: ./deploy-dashboards.sh <service-account-token>
#
# Create token at:
#   bavanchun.grafana.net → Administration → Service accounts
#   → Add service account (Editor) → Add service account token
# =============================================================

set -euo pipefail

GRAFANA_URL="https://bavanchun.grafana.net"
TOKEN="${1:-}"
DASHBOARDS_DIR="$(dirname "$0")/dashboards"
FOLDER_TITLE="TC LLM Generator"

if [[ -z "$TOKEN" ]]; then
  echo "ERROR: Service Account Token required."
  echo "Usage: $0 <glsa_token>"
  exit 1
fi

AUTH_HEADER="Authorization: Bearer $TOKEN"

# ── 1. Create folder ──────────────────────────────────────────
echo "Creating folder '$FOLDER_TITLE'..."
FOLDER_RESP=$(curl -sf -X POST "$GRAFANA_URL/api/folders" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  -d "{\"title\": \"$FOLDER_TITLE\"}" 2>/dev/null || true)

FOLDER_UID=$(echo "$FOLDER_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('uid',''))" 2>/dev/null || true)

# If folder already exists, fetch its UID
if [[ -z "$FOLDER_UID" ]]; then
  FOLDER_UID=$(curl -sf "$GRAFANA_URL/api/folders" \
    -H "$AUTH_HEADER" 2>/dev/null \
    | python3 -c "import sys,json; folders=json.load(sys.stdin); [print(f['uid']) for f in folders if f['title']=='$FOLDER_TITLE']" 2>/dev/null | head -1 || true)
fi

echo "  Folder UID: ${FOLDER_UID:-<root>}"

# ── 2. Deploy each dashboard JSON ─────────────────────────────
for DASH_FILE in "$DASHBOARDS_DIR"/*.json; do
  DASH_TITLE=$(python3 -c "import sys,json; d=json.load(open('$DASH_FILE')); print(d['title'])" 2>/dev/null)
  echo ""
  echo "Deploying: $DASH_TITLE"
  echo "  File: $(basename $DASH_FILE)"

  # Wrap dashboard JSON in the API payload format
  PAYLOAD=$(python3 - <<EOF
import json

with open("$DASH_FILE") as f:
    dashboard = json.load(f)

# Remove id so Grafana auto-assigns; keep uid if present for idempotent updates
dashboard.pop("id", None)

payload = {
    "dashboard": dashboard,
    "folderUid": "$FOLDER_UID",
    "overwrite": True,
    "message": "Deployed by deploy-dashboards.sh"
}
print(json.dumps(payload))
EOF
  )

  RESULT=$(curl -sf -X POST "$GRAFANA_URL/api/dashboards/db" \
    -H "$AUTH_HEADER" \
    -H "Content-Type: application/json" \
    -d "$PAYLOAD" 2>/dev/null)

  STATUS=$(echo "$RESULT" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('status','?'))" 2>/dev/null || echo "error")
  DASH_URL=$(echo "$RESULT" | python3 -c "import sys,json; d=json.load(sys.stdin); print('$GRAFANA_URL' + d.get('url',''))" 2>/dev/null || echo "")

  if [[ "$STATUS" == "success" ]]; then
    echo "  ✅ $STATUS → $DASH_URL"
  else
    echo "  ❌ Failed: $RESULT"
  fi
done

echo ""
echo "Done. Open: $GRAFANA_URL/dashboards"

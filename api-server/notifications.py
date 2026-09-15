import os

import dotenv
import httpx
import google.auth.transport.requests
from google.oauth2 import service_account

dotenv.load_dotenv()

FCM_PROJECT_ID = os.getenv('FCM_PROJECT_ID')
FCM_CREDENTIALS_FILE = os.getenv('FCM_CREDENTIALS_FILE')

def _access_token() -> str:
    creds = service_account.Credentials.from_service_account_file(
        FCM_CREDENTIALS_FILE, scopes=['https://www.googleapis.com/auth/firebase.messaging']
    )
    creds.refresh(google.auth.transport.requests.Request())
    return creds.token

def send_push(token: str, title: str, body: str, data: dict | None = None) -> bool:
    if not FCM_PROJECT_ID or not FCM_CREDENTIALS_FILE:
        print(f'[push to {token}] {title}: {body}')  # dev fallback, FCM not configured
        return True

    payload = {
        'message': {
            'token': token,
            'notification': {'title': title, 'body': body},
            'data': {k: str(v) for k, v in (data or {}).items()},
        }
    }
    r = httpx.post(
        f'https://fcm.googleapis.com/v1/projects/{FCM_PROJECT_ID}/messages:send',
        headers={'Authorization': f'Bearer {_access_token()}'},
        json=payload, timeout=5
    )
    return r.status_code == 200

def send_push_to_user(user_id: int, title: str, body: str, data: dict | None = None) -> None:
    from dao import DeviceTokenDao
    for token in DeviceTokenDao().get_tokens(user_id):
        send_push(token, title, body, data)

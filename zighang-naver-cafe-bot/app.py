import requests
from flask import Flask, redirect, request, jsonify
from urllib.parse import urlencode
from base64 import decode

app = Flask(__name__)
# 네이버 로그인 엔드포인트
@app.route("/naver") 
def NaverLogin():
    print("/naver")
    client_id = "_e4TY36uU2uiDjoAWmEY"
    redirect_uri = "http://localhost:8080/callback"
    url = f"https://nid.naver.com/oauth2.0/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code"
    print(url)
    return redirect(url)

# 콜백 엔드포인트
@app.route("/callback")
def callback():
    print("/callback")
    params = request.args.to_dict()
    code = params.get("code")

    client_id = "_e4TY36uU2uiDjoAWmEY"
    client_secret = "NLSMmIDcMX"
    redirect_uri = "http://localhost:8080/callback"

    # 토큰 요청
    token_request = requests.get(
        f"https://nid.naver.com/oauth2.0/token",
        params={
            "grant_type": "authorization_code",
            "client_id": client_id,
            "client_secret": client_secret,
            "code": code
        }
    )
    token_json = token_request.json()
    print(token_json)

    access_token = token_json.get("access_token")
    
    if not access_token:
        return "Failed to obtain access token", 400

    # 프로필 요청
    profile_request = requests.get(
        "https://openapi.naver.com/v1/nid/me",
        headers={"Authorization": f"Bearer {access_token}"}
    )
    profile_data = profile_request.json()

    return write_post(access_token)

def write_post(access_token):
    token = access_token
    clubid = "31332056"
    menuid = "1"
    subject = "[subject] 네이버 Cafe api Test Python"
    content = "[content] 네이버 Cafe api Test Python"
    encoded_subject = urlencode({"subject": subject}, encoding='UTF-8', doseq=True)
    encoded_content = urlencode({'content': content},encoding='UTF-8', doseq=True)
    print(encoded_subject)
    
    if not token:
        return jsonify({"error": "Token is required"}), 400
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/x-www-form-urlencoded"
    }
    url = f"https://openapi.naver.com/v1/cafe/{clubid}/menu/{menuid}/articles"
    post_data = {
        "subject": encoded_subject,
        "content": encoded_content
    }
    
    try:
        response = requests.post(url, headers=headers, data=post_data)
        if response.status_code == 200:
            print("success write")
            return jsonify(response.json()), 200
        else:
            print("error write")
            return jsonify({"error": response.status_code, "message": response.text}), response.status_code
    except requests.exceptions.RequestException as e:
        return jsonify({"error": "Request failed", "message": str(e)}), 500

if __name__ == "__main__":
    app.run(debug=True, host='127.0.0.1', port=8080)

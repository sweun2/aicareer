import requests
from flask import Flask, redirect, request, jsonify
from urllib.parse import urlencode
from base64 import decode

app = Flask(__name__)
# 네이버 로그인 엔드포인트
@app.route('/naver') 
def NaverLogin():
    print('/naver')
    client_id = 'yqGyqTUE5Mu8UDYUBbQL'
    redirect_uri = 'http://localhost:8080/callback'
    url = f'https://nid.naver.com/oauth2.0/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code'
    print(url)
    return redirect(url)

# 콜백 엔드포인트
@app.route('/callback')
def callback():
    print('/callback')
    params = request.args.to_dict()
    code = params.get('code')

    client_id = 'yqGyqTUE5Mu8UDYUBbQL'
    client_secret = 'llkZksXU9z'
    redirect_uri = 'http://localhost:8080/callback'

    # 토큰 요청
    token_request = requests.get(
        f'https://nid.naver.com/oauth2.0/token',
        params={
            'grant_type': 'authorization_code',
            'client_id': client_id,
            'client_secret': client_secret,
            'code': code
        }
    )
    token_json = token_request.json()
    print(token_json)

    access_token = token_json.get('access_token')
    
    if not access_token:
        return 'Failed to obtain access token', 400

    # 프로필 요청
    profile_request = requests.get(
        'https://openapi.naver.com/v1/nid/me',
        headers={'Authorization': f'Bearer {access_token}'}
    )
    profile_data = profile_request.json()

    return write_post(access_token)

def write_post(access_token):
    token = access_token
    clubid = '31332841'
    menuid = '1'
    subject = '제목 : ~ 날짜 (24.11.20) | 기업명 공고제목 '
    content = '''
    <div data-v-4e6bf9d7=''><div data-v-4e6bf9d7='' class='article_viewer'><!----><!----><!----><!----><div data-v-4e6bf9d7=''><div class='content CafeViewer'><div class='se-viewer se-theme-default' lang='ko-KR'>
    <!-- SE_DOC_HEADER_START -->
    <!--@CONTENTS_HEADER-->
    <!-- SE_DOC_HEADER_END -->
    <div class='se-main-container'>
                <div class='se-component se-text se-l-default' id='SE-22d69798-4ccd-11ef-bdd1-8f360399e0b0'>
                    <div class='se-component-content'>
                        <div class='se-section se-section-text se-l-default'>
                            <div class='se-module se-module-text'>
                                    <!-- SE-TEXT { --><p class='se-text-paragraph se-text-paragraph-align- ' style='' id='SE-22d69799-4ccd-11ef-bdd1-a9eeb05e16ef'><span style='' class='se-fs- se-ff-   ' id='SE-22d6979a-4ccd-11ef-bdd1-c3c23f5c9e3f'>채용 공고 </span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class='se-text-paragraph se-text-paragraph-align- ' style='' id='SE-22d6979b-4ccd-11ef-bdd1-03b1675b2793'><span style='' class='se-fs- se-ff-   ' id='SE-22d6979c-4ccd-11ef-bdd1-91320eaf0661'> - URL (공고 링크) : </span><span style='' class='se-fs- se-ff-   ' id='SE-ab61a123-8d45-4263-bfca-4cde836f3a5e'><a href='https://zighang.com/recruitment/2e7efafd-6ea3-4a75-998e-3ceb233207d3' class='se-link' target='_blank'>https://zighang.com/recruitment/2e7efafd-6ea3-4a75-998e-3ceb233207d3</a></span></p><!-- } SE-TEXT -->
                            </div>
                        </div>
                    </div>
                </div>                <div class='se-component se-oglink se-l-large_image __se-component' id='SE-b14bf8c7-556e-409e-8899-06cb4361e2f7'>
                    <div class='se-component-content'>
                        <div class='se-section se-section-oglink se-l-large_image se-section-align-'>
                            <div class='se-module se-module-oglink'>
                                <a href='https://zighang.com/recruitment/2e7efafd-6ea3-4a75-998e-3ceb233207d3' class='se-oglink-thumbnail' target='_blank'>
                                    <img src='https://dthumb-phinf.pstatic.net/?src=%22https%3A%2F%2Fd2juy7qzamcf56.cloudfront.net%2F2024-10-31%2F10e1528b-520a-460f-bf72-b934c6c1807c.jpg%22&amp;type=ff500_300' class='se-oglink-thumbnail-resource' alt=''>
                                </a>
                                <a href='https://zighang.com/recruitment/2e7efafd-6ea3-4a75-998e-3ceb233207d3' class='se-oglink-info' target='_blank'>
                                    <div class='se-oglink-info-container'>
                                        <strong class='se-oglink-title'>당근 채용 | Software Engineer, Backend - Local Maps - IT</strong>
                                        <p class='se-oglink-summary'>제목 : Software Engineer, Backend - Local Maps | 회사 : 당근 | 회사 유형 : 유니콘 | 회사 주소 : 서울특별시 강남구 테헤란로 123, 5층 (역삼동) | 채용 유형 : 정규직 | 학력 조건 : 학력 무관 | 경력 조건 : 1~3년차 | 지역 : 서울 | 마감 기간 유형 : 상시 채용 | 모집 마감일 : 2025-01-14 | 직군 : 백엔드</p>
                                        <p class='se-oglink-url'>zighang.com</p>
                                    </div>
                                </a>
                            </div>
                        </div>
                    </div>
                    <script type='text/data' class='__se_module_data' data-module='{&quot;type&quot;:&quot;v2_oglink&quot;, &quot;id&quot; :&quot;SE-b14bf8c7-556e-409e-8899-06cb4361e2f7&quot;, &quot;data&quot; : {&quot;link&quot; : &quot;https://zighang.com/recruitment/2e7efafd-6ea3-4a75-998e-3ceb233207d3&quot;, &quot;isVideo&quot; : &quot;false&quot;, &quot;thumbnail&quot; : &quot;https://dthumb-phinf.pstatic.net/?src=%22https%3A%2F%2Fd2juy7qzamcf56.cloudfront.net%2F2024-10-31%2F10e1528b-520a-460f-bf72-b934c6c1807c.jpg%22&amp;type=ff500_300&quot;}}'></script>
                </div>                <div class='se-component se-text se-l-default' id='SE-248dafc0-58d6-4df5-915f-011cd8dca974'>
                    <div class='se-component-content'>
                        <div class='se-section se-section-text se-l-default'>
                            <div class='se-module se-module-text'>
                                    <!-- SE-TEXT { --><p class='se-text-paragraph se-text-paragraph-align- ' style='' id='SE-f004ebf6-54da-4f7d-b310-89d84b4291a7'><span style='' class='se-fs- se-ff-   ' id='SE-28402db3-2262-4038-bf0c-86f7b62488f9'>​</span></p><!-- } SE-TEXT -->
                            </div>
                        </div>
                    </div>
                </div>    </div>
</div>
</div><!----><div class='AttachFileIssueLayer' style='display: none;'><!----></div></div><!----></div></div>
    '''
    encoded_subject = urlencode({'subject': subject}, encoding='UTF-8', doseq=True)[len('subject')+1:]
    encoded_content = urlencode({'content': content}, encoding='UTF-8', doseq=True)[len('content')+1:]
    
    if not token:
        return jsonify({'error': 'Token is required'}), 400
    headers = {
        'Authorization': f'Bearer {token}',
        'Content-Type': 'application/x-www-form-urlencoded'
    }
    url = f'https://openapi.naver.com/v1/cafe/{clubid}/menu/{menuid}/articles'
    post_data = {
        'subject': encoded_subject,
        'content': encoded_content
    }
    
    try:
        response = requests.post(url, headers=headers, data=post_data)
        if response.status_code == 200:
            print('success write')
            return jsonify(response.json()), 200
        else:
            print('error write')
            return jsonify({'error': response.status_code, 'message': response.text}), response.status_code
    except requests.exceptions.RequestException as e:
        return jsonify({'error': 'Request failed', 'message': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=8080)

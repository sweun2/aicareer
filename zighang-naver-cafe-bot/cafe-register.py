subject = "[subject] 네이버 Cafe api Test Python"
subject = str(subject.encode('utf-8')).encode('cp949')
print(type(subject))
subject =subject.decode('utf-8')
print(subject)


content = "[content] 네이버 Cafe api Test Python"
content = str(content.encode('utf-8')).encode('cp949').decode('utf-8')

print(content)
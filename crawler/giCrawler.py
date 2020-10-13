from robobrowser import RoboBrowser

browser = RoboBrowser(parser='html.parser')
browser.open('https://www.google.co.kr/imghp?hl=ko')

form = browser.get_form(action='https://www.google.co.kr/search')
form['q'] = '더K호텔서울'
browser.submit_form(form, list(form.submit_fields.values()))

for a in browser.select('.islrc > div:nth-child(1) > a:nth-child(1) > div:nth-child(1) > img:nth-child(1)'):
    print(a.get(''))
    print()


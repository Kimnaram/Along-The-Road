import requests
import re
import lxml.html
import MySQLdb

def crawling():
    q = "더k호텔서울"
    url="https://www.google.com/search?q=" + q + "&tbm=isch&bih=732"

    print(url)

    list_page = requests.get(url)
    root = lxml.html.fromstring(list_page.content)
    for everything in root.cssselect('body'):
        images = everything.cssselect('.islrc > div:nth-child(1) > a:nth-child(1) > div:nth-child(1) > img:nth-child(1)')
        if not images:
            image = ' '
        elif images:
            image = images[0].attrib('src')
            print(image)

def main():
    crawling()

main()

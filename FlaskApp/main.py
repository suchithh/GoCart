import urllib.request
import gunicorn
from bs4 import BeautifulSoup
from flask import Flask, request, jsonify
import re
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import os
import time
import json

app = Flask(__name__)

#Header
header = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36'


data = []
response={}
results={}
pinresp={}
numResults = 0
imgquery = fl_quer = am_quer = gr_quer = pa_quer = 0

#Selenium starter code
options = Options()
options.binary_location = os.environ.get("GOOGLE_CHROME_BIN")
options.add_argument('--disable-dev-shm-usage')
options.add_argument('--no-sandbox')
options.add_argument('--headless')
options.add_argument('--disable-gpu')
options.add_argument("--log-level=1") 


@app.route('/api', methods=['GET'])
def respond():
    response.clear()
    pinresp = {}
    global numResults
    global imgquery
    global fl_quer, am_quer, gr_quer, pa_quer
    #Search Query
    if 'id' in request.args:
        id = request.args['id']
    else:
        return "Error: No id field provided. Please specify an id."
    #Image Query
    if 'image' in request.args:
        imgquery = request.args['image']
        if imgquery=="0" or imgquery=="1": 
            imgquery=int(imgquery)
        else:
            imgquery=0
    else:
        imgquery = 0
    #Individual Site Query
    am_quer = pa_quer = fl_quer = gr_quer = 1
    if 'am' in request.args:
        am_quer = int(request.args['am'])
    if 'fl' in request.args:
        fl_quer = int(request.args['fl'])
    if 'gr' in request.args:
        gr_quer = int(request.args['gr'])
    if 'pa' in request.args:
        pa_quer = int(request.args['pa'])
        
    #Number of results
    if 'results' in request.args:
        try:
            numResults = int(request.args['results'])
        except:
            numResults = 100    
    else:
        numResults = 100
    #Pincode Query
    if 'pin' in request.args:
        try:
            pin = str(int(request.args['pin']))
            pincode(pin)
        except:
            pass

    #Print ID for troubleshooting
    print("Got ID : ",id)
    data.clear()
    return(check(id.replace(' ','%20')))

def check(code):
    start_time = time.time()
    count = 1
    print ("Image Query :",imgquery)
    print (fl_quer,am_quer,gr_quer,pa_quer)
    if fl_quer == 1:
        flipkart(code)
    if am_quer == 1:
        amazon(code)
    if gr_quer == 1:
        grofers(code)
    if pa_quer == 1:
        paytm(code)
    #Sort data
    for res in sorted(data, key = lambda i: float(i['cost'].replace(",","")[1:])):
        if count<=numResults:
            response[int(count)]=res
        else:
            break
        count+=1
    #Append responses to results dictionary
    results["data"]=response
    results["results"]=count-1
    results["pindata"]=pinresp
    results["time taken"]=("%s seconds" % (time.time() - start_time))
    print("--- %s seconds ---" % (time.time() - start_time))
    
    return jsonify(results)

#Pincode check  
def pincode(pin):
    global pinresp
    pinresp = json.loads(urllib.request.urlopen('https://api.postalpincode.in/pincode/'+pin).read())
    

def flipkart(term):
    imgcount = 1
    links = []
    site = urllib.request.Request("https://www.flipkart.com/search?q="+term, headers={'User-Agent': header})
    html = urllib.request.urlopen(site)
    html = html.read()
    soup = BeautifulSoup(html, 'html.parser')
    #Parse Data
    link_count=0
    blocks = soup.find_all('div', {'class' : '_4ddWXP'})[:13]
    #Alternate Layout
    if len(blocks) == 0:
        blocks_alt = soup.find_all('div', {'class' : '_1xHGtK _373qXS'})[:13]

        #Alternate layout 2
        if len(blocks_alt)==0:
            blocks_alt2 = soup.find_all('div', {'class' : '_2kHMtA'})[:13]

            for block in blocks_alt2:
                link = "https://www.flipkart.com"+block.find('a', {'class' : '_1fQZEK'}).get('href')
                name = block.find('div', {'class' : '_4rR01T'}).get_text()
                try:
                    rating = str(float(block.find('div', {'class' : '_3LWZlK'}).get_text()))+"/5"
                except:
                    rating = "null"
                cost = block.find('div', {'class' : '_30jeq3 _1_WHN1'}).get_text()
                data.append({"site":"flipkart",
                        "name":name,
                        "cost":cost,
                        "link":link,
                        "rating":rating,
                        "image":"null",
                        "currency":"INR"})



        for block in blocks_alt:
            link = "https://www.flipkart.com"+block.find('a', {'class' : 'IRpwTa'}).get('href')
            name = block.find('a', {'class' : 'IRpwTa'}).get('title')
            try:
                subtitle = ", "+block.find('div', {'class' : '_2WkVRV'}).get_text()
            except:
                subtitle = ""
            try:
                rating = str(float(block.find('div', {'class' : 'hGSR34'}).get_text()))+"/5"
            except:
                rating = "null"
            cost = block.find('div', {'class' : '_30jeq3'}).get_text()
            data.append({"site":"Flipkart",
                    "name":name+subtitle,
                    "cost":cost,
                    "link":link,
                    "rating":rating,
                    "image":"null",
                    "currency":"INR"})

    #Main Layout
    for block in blocks:
        link = "https://www.flipkart.com"+block.find('a', {'class' : 's1Q9rs'}).get('href')
        name = block.find('a', {'class' : 's1Q9rs'}).get('title')
        try:
            subtitle = ", "+block.find('div', {'class' : '_3Djpdu'}).get_text()
        except:
            subtitle = ""
        try:
            rating = str(float(block.find('div', {'class' : '_3LWZlK'}).get_text()))+"/5"
        except:
            rating = "null"
        cost = block.find('div', {'class' : '_30jeq3'}).get_text()
        #Selenium
        if (imgquery==1 and imgcount<2):
            driver = webdriver.Chrome(executable_path=os.environ.get("CHROMEDRIVER_PATH"),options=options)
            driver.get(link)
            imgs = driver.find_elements_by_tag_name('img')
            for img in imgs:
                if 'https://rukminim1.flixcart.com/image/' in img.get_attribute('srcset'):
                    image = img.get_attribute('src')
            imgcount+=1
        else:
            image = "null"
            imgcount+=1
        #       

        data.append({"site":"Flipkart",
                "name":name+subtitle,
                "cost":cost,
                "link":link,
                "rating":rating,
                "image":image,
                "currency":"INR"})



def amazon(term):
    site = urllib.request.Request("https://www.amazon.in/s?k="+term, headers={'User-Agent': header})
    html = urllib.request.urlopen(site)
    html = html.read()
    soup = BeautifulSoup(html, 'html.parser')
    #Parse Data
    blocks = soup.find_all('div', {'class' : 's-include-content-margin s-border-bottom s-latency-cf-section'})[:13]

    #Alternate Layout
    if len(blocks)==0:
        blocks_alt = soup.find_all('div', {'class' : 'sg-col-4-of-12 s-result-item s-asin sg-col-4-of-16 sg-col sg-col-4-of-20'})[:13]
        for block in blocks_alt:    
            name = block.find('span', {'class' : 'a-size-base-plus a-color-base a-text-normal'}).get_text()
            if (name=='Sponsored'):
                continue
            try:
                link = "https://www.amazon.in"+block.find('a', {'class': 'a-link-normal a-text-normal'}).get('href')
            except:
                continue
            try:
                rating = block.find('span', {'class', 'a-icon-alt'}).get_text().replace(" out of ","/").replace(" stars","")
            except:
                rating = "null"
            try:
                cost = '\u20b9'+block.find('span', {'class' : 'a-price-whole'}).get_text().replace('.','')
            except:
                cost = "rinf"
            try:
                image = block.find('img', {'class' : 's-image'}).get("src")
            except:
                image = "null"
            data.append({"site":"Amazon",
                "rating":rating,
                "name":name,
                "cost":cost,
                "currency":"INR",
                "link":link,
                "image":image})  

    #Main Layout
    for block in blocks:
        name = block.find('span', {'class' : 'a-size-medium a-color-base a-text-normal'}).get_text()
        if (name=='Sponsored'):
            continue
        try:
            link = "https://www.amazon.in"+block.find('a', {'class': 'a-link-normal a-text-normal'}).get('href')
        except:
            continue
        try:
            rating = block.find('span', {'class', 'a-icon-alt'}).get_text().replace(" out of ","/").replace(" stars","")
        except:
            rating = "null"
        try:
            cost = '\u20b9'+block.find('span', {'class' : 'a-price-whole'}).get_text().replace('.','')
        except:
            cost = "rinf"
        try:
            # imageblock = soup.find_all('img', {'class' : 's-image'})[i].get("srcset")
            # image = re.findall('http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', imageblock)[3]
            image = block.find('img', {'class' : 's-image'}).get('src')
        except:
            image = "null"
        data.append({"site":"Amazon",
            "rating":rating,
            "name":name,
            "cost":cost,
            "currency":"INR",
            "link":link,
            "image":image})   


def grofers(term):
    site = urllib.request.Request("https://www.grofers.com/s/?q="+term, headers={'User-Agent': header})
    html = urllib.request.urlopen(site)
    html = html.read()
    soup = BeautifulSoup(html, 'html.parser')
    #Parse data
    blocks = soup.find_all('div', {'class' : 'plp-product'})[:5]
    i=0
    for block in blocks:
        name = block.find('div', {'class' : 'plp-product__name--box'}).get_text()
        subtitle = ", "+block.find('div', {'class' : 'plp-product__quantity'}).get('title')
        cost = block.find('span', {'class' : 'plp-product__price--new'}).get_text()
        link = "https://www.grofers.com"+soup.find_all('a', {'data-test-id' : 'plp-product'})[i].get('href')
        image = "https://cdn.grofers.com/app/images/products/full_screen/pro_"+link.split('/')[6]+".jpg"
        i+=1
        data.append({"site":"Grofers",
            "rating":"null",
            "name":name+subtitle,
            "cost":cost,
            "currency":"INR",
            "link":link,
            "image":image})

def paytm(term):
    site = urllib.request.Request("https://paytmmall.com/shop/search?q="+term, headers={'User-Agent': header})
    html = urllib.request.urlopen(site)
    html = html.read()
    soup = BeautifulSoup(html, 'html.parser')
    #Parse data
    blocks = soup.find_all('div', {'class' : '_3WhJ'})[:5]
    for block in blocks:
        name = block.find('img', {'role' : 'presentation'}).get('alt')
        link = 'https://paytmmall.com'+block.find('a', {'class' : '_8vVO'}).get('href')
        cost = '\u20b9'+block.find('div', {'class' : '_1kMS'}).get_text()
        image = block.find('img', {'role' : 'presentation'}).get('src')
        data.append({"site":"Paytm Mall",
            "rating":"null",
            "name":name,
            "cost":cost,
            "currency":"INR",
            "link":link,
            "image":image})


#BigBasket - In Progress
def bigb(term):
    site = urllib.request.Request("https://www.bigbasket.com/ps/?q="+term, headers={'User-Agent': header})
    html = urllib.request.urlopen(site)
    html = html.read()
    soup = BeautifulSoup(html, 'html.parser')
    #Parse data
    blocks = soup.find_all('div', {'class' : 'clearfix'})[:5]
    print (blocks)


#Snapdeal - In Progress
def snapdeal(term):
    site = urllib.request.Request("https://www.snapdeal.com/search?keyword="+term, headers={'User-Agent': header})
    html = urllib.request.urlopen(site)
    html = html.read()
    soup = BeautifulSoup(html, 'html.parser')
    print (soup)
    #Parse Data
    blocks = soup.find_all('div', {'class' : 'col-xs-6  favDp product-tuple-listing js-tuple'})[:5]
    for block in blocks:
        name = block.find('source', {'class' : 'product_image'}).get('title')
        image = block.find('source', {'class' : 'product_image'}).get('srcset')
        cost = '\u20b9'+block.find('span', {'class' : 'lfloat product-price'}).get('display-price')
        link = block.find('a', {'class' : 'dp-widget-link hashAdded'}).get('href')
        print (name,image,cost,block)




#Test route
@app.route('/')
def index():
    return "<h1>Scrape server</h1>"


#Main Function
if __name__ == '__main__':
    # Threaded option to enable multiple instances for multiple user access support
    app.run(threaded=True, port=5000)


#Function Test
# check('8902519010438')
# amazon('intel')
# imgquery = 0
# flipkart('redmi')
# snapdeal('book')
# grofers('parle')
# bigb("soap")
# paytm('redmi')
# print (data)

#Sample barcode numbers
#Dove shampoo 8901030704307
#clarks table 9788177581515
#papergrid    8906028930775
#classmate    8902519010438
#i3-9100f     735858414258
#i3-9100      5032037156264
#SA           9789389174779

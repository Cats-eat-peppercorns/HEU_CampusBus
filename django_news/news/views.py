#TODO:我要实现锁的使用，不然写入数据库和读会造成脏数据等情况
import base64
import json
import os
from django.db import connection
from django.utils import timezone
from django.forms import FloatField, model_to_dict
from django.http import FileResponse, HttpResponse, Http404,JsonResponse ,HttpResponseServerError,JsonResponse
from django.shortcuts import render
from news import models
from news.models import *
from django.core.files.storage import FileSystemStorage
import contextlib
from django.db import connection
from pymysql import NULL
from decimal import Decimal
from django.db.models import ExpressionWrapper, F, FloatField

from django.core import signing
from django.contrib.sessions.models import Session
from django.contrib.sessions.backends.db import SessionStore
from django.http import HttpResponseBadRequest


def index(request):
    context = {
        'news_list': [
            {
                "title": "Android后端web",
                "content": "由Django框架编写",
            },
        ]
    }
    return render(request, 'news/index.html', context=context)
def isLogin(request):
    accountGet=request.POST.get('account', '') 
    passwordGet=request.POST.get('password', '')
    session = SessionStore()
    session['account'] = accountGet
    session_data_fernet = session.encode(session._get_session(no_load=True))
    session_new =Session.objects.filter(session_data=session_data_fernet)
    # 先获取对应账号的user信息
    user=User.objects.raw("\
        SELECT * FROM user\
        WHERE account = %s LIMIT 1",
        [accountGet])
    # 如果对应账号在数据库存在
    if user[0]!=NULL:
        # 先判断密码是否正确
        if user[0].password==passwordGet:
            # 密码如果正确判断对应session是否存在
            if user[0].session!=None:
                 with connection.cursor() as cursor:
                    #存在就删去原本的session
                    cursor.execute(" \
                        DELETE FROM django_session \
                        WHERE session_key=(SELECT session_id FROM user WHERE account=%s)",
                        [accountGet])
            # 产生一个新的session并保存到request
            request.session['account'] = accountGet
            request.session.save()
            session_key_value=request.session.session_key
            # 将user的登陆状态改为true，并保存新的session到数据库
            with connection.cursor() as cursor:
                cursor.execute(" \
                    UPDATE user SET \
                    islogin=1,\
                    session_id=%s \
                        WHERE account=%s",
                [session_key_value,accountGet])
            res = model_to_dict(user[0])
            response = JsonResponse(res,safe=False)
            return response    
        
def exitAccount(request):
    accountGet=request.POST.get('account', '')
    with connection.cursor() as cursor:
        cursor.execute("\
            UPDATE user SET isLogin=0 WHERE account=%s",
            [accountGet])
        cursor.execute("\
            DELETE FROM django_session \
            WHERE session_key=(SELECT session_id FROM user WHERE account=%s)",
            [accountGet])
    return HttpResponse('OK')

def autoLogin(request):
    if request.method=='POST':
        accountGet=request.POST.get('account','')
        User.objects.filter(account=accountGet).update(islogin=1)
        return HttpResponse('OK')
    return HttpResponse('OK')

def register(request):
    if request.method=='POST':
        nameGet=request.POST.get('name','')
        accountGet=request.POST.get('account', '') 
        passwordGet=request.POST.get('password', '')
        avatarGet=request.POST.get('avatar','')
        if User.objects.filter(name=nameGet).exists()==False:
            if User.objects.filter(account=accountGet).exists()==False:
                User.objects.create(name=nameGet,account=accountGet,password=passwordGet,avatar=avatarGet,islogin='0')
                return HttpResponse('OK')
            return HttpResponse('OK')
        return HttpResponse('OK')

latitudeNew='0'
longitudeNew='0'

def getLocation(request):
    if request.method=='GET':
        string = '{"latitude": '+ latitudeNew + ',''"longitude": '+ longitudeNew +'}'
        json_data = json.loads(string)
        print(json_data)
        return JsonResponse(json_data,safe=False)
def sendLocation(request):
    global latitudeNew
    latitudeNew=request.POST.get('latitude','')
    global longitudeNew
    longitudeNew=request.POST.get('longitude','')
    session_id = request.COOKIES.get('sessionid')
    session =Session.objects.filter(session_key=session_id).first()
    try:
        session_data = session.session_data
        print(session_data)  
        return  HttpResponse('OK')
    except session_id not in session:
        return HttpResponseServerError("error")
    
def duplicateLoginsTest(request):
    accountGet=request.POST.get('account', '')
    session_id_Get= request.COOKIES.get('sessionid')
    # print(session_id_Get)
    user=User.objects.raw("\
        SELECT * FROM user\
        WHERE account = %s LIMIT 1",
        [accountGet])
    # if user[0].session.session_key!=session_id_Get:
    if user[0].session.session_key!=session_id_Get:
        return  HttpResponse('10')
    return HttpResponse('11')
def download_database(request):
    file_path = 'G:\Django\django_news\media\Location.db'  # 替换成你的数据库文件路径
    try:
        response = FileResponse(open(file_path, 'rb'))
        response['content_type'] = "application/octet-stream"
        response['Content-Disposition'] = 'attachment; filename=' + os.path.basename(file_path)
        return response
    except Exception:
        raise Http404
def getLocationImage(request):
    IvPath=request.POST.get('IvPath','')
    image_path = os.path.join('G:\Django\django_news\media', 'LocationImage', '{}.jpg'.format(IvPath))

    with open(image_path, 'rb') as f:
        return HttpResponse(f.read(), content_type='image/png')

def driver_register(request):
    if request.method=='POST':
        nameGet=request.POST.get('name','')
        accountGet=request.POST.get('account', '') 
        passwordGet=request.POST.get('password', '')
        schoolGet_name=request.POST.get('school','')
        lineGet_name=request.POST.get('line','')
        phoneGet=request.POST.get('phone','')
        if Driver.objects.filter(account=accountGet).exists()==False:
            schoolGet=School.objects.filter(school_name=schoolGet_name)
            #print(schoolGet.exists(),lineGet.exists())
            if schoolGet.exists():
                lineGet=Bus_line.objects.filter(line_school=schoolGet[0],line_name=lineGet_name)
                if lineGet.exists():
                    #print(schoolGet[0].school_id)
                    new_driver=Driver.objects.create(phone=phoneGet,name=nameGet,account=accountGet,password=passwordGet,driver_school=schoolGet[0],driver_line=lineGet[0])
                    Driver_location.objects.create(location_driver=new_driver,longitude=0,latitude=0)
                    return HttpResponse('OK')       
                else:
                    return HttpResponseServerError('该线路不存在')
            else:
                return  HttpResponseServerError('该学校不存在')
        else:
            return HttpResponseServerError('账号已存在')

def driver_login(request):
    if request.method=='POST':
        accountGet=request.POST.get('account', '') 
        passwordGet=request.POST.get('password', '') 
        #print(accountGet)
        driver=Driver.objects.filter(account=accountGet)  #得到一个对象列表 这个列表内容是对象，也就是表中找到的对象
        #print(res)
        if driver.exists():
            school_name_new=School.objects.filter(school_name=driver[0].driver_school)[0].school_name
            line_name_new=Bus_line.objects.filter(line_school=driver[0].driver_school,line_name=driver[0].driver_line)[0].line_name
            new_data={'school_name_new':school_name_new,'line_name_new':line_name_new}
            res=model_to_dict(driver[0])  #将model转字典
            res.update(new_data)  #将名字填进去
            if driver[0].password==passwordGet:  
                return JsonResponse(res,safe=False)
            else:
                return HttpResponseServerError('密码错误')
        else:
            return HttpResponseServerError('账号不存在')

def driver_locationSend(request):  #处理发送来的司机坐标   并推算出其行驶方向和目标站点
    if request.method=='POST':
        driverGet=request.POST.get('driver_account', '') 
        latitudeGet=request.POST.get('latitude', '') 
        longitudeGet=request.POST.get('longitude', '') 
        get_nearest_station(latitudeGet,longitudeGet,driverGet)
        driver=Driver.objects.filter(account=driverGet)
        if(driver.exists):
            # Driver_location.objects.filter(location_driver=driver[0]).update(latitude=latitudeGet,longitude=longitudeGet)
            # Driver_location.save()
            driver_location = Driver_location.objects.get(location_driver=driver[0])
            driver_location.latitude = latitudeGet
            driver_location.longitude = longitudeGet
            driver_location.save()  # 手动保存对象，触发DateTimeField自动更新
            print(latitudeGet,longitudeGet)
        return HttpResponse('OK')

def get_nearest_station(latitudeGet,longitudeGet,driverGet): #此函数目的是通过新获得的时间及坐标来算出最近的站点以及下一站
    latitude=latitudeGet
    longitude=longitudeGet
    close_station=Bus_station.objects.raw('SELECT station_id,\
                                    ST_DISTANCE(POINT(station_latitude, station_longitude),POINT(%s, %s)) AS distance \
                                    FROM bus_station ORDER BY distance limit 1',[latitude,longitude])
    driver=Driver.objects.filter(account=driverGet)
    if(driver.exists()):
        driver_location = Driver_location.objects.get(location_driver=driver[0])
        if(driver_location.now_station is not None and driver_location.now_station!=close_station[0]): 
            #目前位置不为空，且新位置和前位置不同
            station_old=Bus_station.objects.get(station_name=driver_location.now_station)
            ntstation=station_old.next_station
            ltstation=station_old.last_station
            nw_station=Bus_station.objects.get(station_name=close_station[0])
            if(ntstation!=close_station[0] and ltstation!=close_station[0]):  
                #若跑的离另一个不是目标站的站近了，有两种可能：1.车刚开动，上次的残留信息还在。//直接在onDestroy时加上清除信息不就行了(已加)
                #2.车出现错乱，不小心离另一个站近了，这时直接忽略掉这个站，直到进行到下一个站
                return
            #如果新站既是之前站的下一站又是之前站的上一站，则说明到达终点站，其下一站应为非上一站的站
            if(ntstation==ltstation):
                if(nw_station.next_station==station_old):
                    driver_location.next_station=nw_station.last_station
                elif(nw_station.last_station==station_old):
                    driver_location.next_station=nw_station.next_station
            elif(ntstation==close_station[0]): #如果新站为之前站的下一站，则定义方向为正，并给出下一站
                true_next=nw_station.next_station
                driver_location.next_station=true_next
            elif(ltstation==close_station[0]): #若为前一站，同理
                true_last=nw_station.last_station
                driver_location.next_station=true_last
        driver_location.now_station=close_station[0]
        driver_location.save()
    return


def driver_on_the_job(request):  #处理司机在岗信息
    if request.method=='POST':
        driverGet=request.POST.get('driver_account', '') 
        ONorOFFGet=(int)(request.POST.get('ONorOFF', ''))
        driver=Driver.objects.filter(account=driverGet)
        #print(ONorOFFGet)
        if(driver.exists):
            # print(ONorOFFGet)
            driver_isworking = Driver_location.objects.get(location_driver=driver[0])
            if ONorOFFGet==1:
                driver_isworking.isworking=True
            else:
                driver_isworking.isworking=False
                driver_isworking.now_station=NULL
                driver_isworking.next_station=NULL
            driver_isworking.save()  # 手动保存对象
        return HttpResponse('OK')

def driver_get_workmates(request):  #获取司机同事信息
    if request.method=='POST':
        driverGet=request.POST.get('driver_account', '') 
        driver_line_get=Driver.objects.get(account=driverGet).driver_line
        driver_school_get=Driver.objects.get(account=driverGet).driver_school
        line_id_get=Bus_line.objects.get(line_name=driver_line_get,line_school=driver_school_get).line_id
        with connection.cursor() as cursor:
            cursor.execute("select * from driver_and_location \
                                  where driver_line_id=%s \
                                  and isworking=%s and account!=%s",[line_id_get,1,driverGet])
            columns = [col[0] for col in cursor.description]
            results = [dict(zip(columns, row)) for row in cursor.fetchall()]
            #results = cursor.fetchall()
            #print(results)
            res=[
                {
                    'name':result['name'],
                    'account':result['account'],
                    'latitude':result['latitude'],
                    'longitude':result['longitude'],
                }
                for result in results
            ]
            #print(res)
            return JsonResponse(res,safe=False)
            #return HttpResponseServerError('账号不存在')
def driver_information_set(request):
    if request.method=='POST':
        nameGet=request.POST.get('name','')
        accountGet=request.POST.get('account', '') 
        passwordGet=request.POST.get('password', '')
        schoolGet_name=request.POST.get('school','')
        lineGet_name=request.POST.get('line','')
        phoneGet=request.POST.get('phone','')
        true_accountGet=request.POST.get('true_account','')
        if Driver.objects.filter(account=true_accountGet).exists():
            if accountGet==true_accountGet or Driver.objects.filter(account=accountGet).exists()==False:
                schoolGet=School.objects.filter(school_name=schoolGet_name)
                #print(schoolGet.exists(),lineGet.exists())
                if schoolGet.exists():
                    lineGet=Bus_line.objects.filter(line_school=schoolGet[0],line_name=lineGet_name)
                    if lineGet.exists():
                        driver = Driver.objects.get(account=true_accountGet)
                        driver.name = nameGet
                        driver.account = accountGet
                        driver.password=passwordGet
                        driver.phone=phoneGet
                        driver.driver_school=schoolGet[0]
                        driver.driver_line=lineGet[0]
                        driver.save()  # 手动保存对象
                        school_name_new=School.objects.filter(school_name=driver.driver_school)[0].school_name
                        line_name_new=Bus_line.objects.filter(line_school=driver.driver_school,line_name=driver.driver_line)[0].line_name
                        new_data={'school_name_new':school_name_new,'line_name_new':line_name_new}
                        res=model_to_dict(driver)  #将model转字典
                        res.update(new_data)  #将名字填进去
                        return JsonResponse(res,safe=False)
                        #return HttpResponse('OK')
                    else:
                        return HttpResponseServerError('该线路不存在')
                else:
                    return  HttpResponseServerError('该学校不存在')
            else:
                return HttpResponseServerError('账号已存在')
        else:
            return HttpResponseServerError('账号不存在')
def delete_driver_account(request):
    if request.method=='POST':
        true_accountGet=request.POST.get('true_account','')
        Driver.objects.filter(account=true_accountGet).delete()
        return HttpResponse('OK')

def driver_getSignedDays(request):  #获取该司机打卡过的日期
    if request.method=='POST':
        true_accountGet=request.POST.get('driver_account','')
        driver=Driver.objects.get(account=true_accountGet)
        clock_in_data=Driver_clockIn.objects.filter(clock_in_driver=driver)
        res=[
            {
                'year':clock.clock_in_date.year,
                'month':clock.clock_in_date.month,
                'day':clock.clock_in_date.day,
            }
            for clock in clock_in_data
        ]
        #print(res)
        return JsonResponse(res,safe=False)

def driver_send_clockIn_date(request): #司机发来签到
    if request.method=='POST':
        true_accountGet=request.POST.get('driver_account','')
        driver=Driver.objects.get(account=true_accountGet)
        Driver_clockIn.objects.create(clock_in_driver=driver,isclock=1)
        return HttpResponse('OK')

def get_nearest_bus(request):
    startStationId = request.POST.get('startStation', '')
    nearestBus = list(Driver_location.objects.raw("\
        SELECT * FROM driver_location\
        WHERE next_station_id = %s LIMIT 1",
        [startStationId]))
    if nearestBus:
        res = {
            'driverId': nearestBus[0].location_driver_id,
            'driverName': nearestBus[0].location_driver.name,
            'driverAccount': nearestBus[0].location_driver.account,
            'driverPhone': nearestBus[0].location_driver.phone,
            'latitude': nearestBus[0].latitude,
            'longitude': nearestBus[0].longitude,
        }
        print(res)
        return JsonResponse(res, safe=False)
    else:
        return JsonResponse({'error': 'No nearest bus found'}, status=404)
def get_nearest_station(request):
    print(request.POST.get('latitude',))
    print(request.POST.get('longitude',))
    latitudeGet=float(request.POST.get('latitude',))
    longitudeGet=float(request.POST.get('longitude'))
    distance_expression = ExpressionWrapper(
        (F('latitude') - latitudeGet)**2 + (F('longitude') - longitudeGet)**2,
        output_field=FloatField()
    )
    # 查询最近的bus_station记录
    nearest_station = Location.objects.filter(istake="1").annotate(distance=distance_expression).order_by('distance').first()
    # 将查询结果转换为JSON格式
    response_data = {
        'locationId': nearest_station.locationid,
        'locationName': nearest_station.locationname,
        'latitude': nearest_station.latitude,
        'longitude': nearest_station.longitude,
        'nearestTake': nearest_station.nearesttake,
        'nearestOff': nearest_station.nearestoff,
        'isTake': nearest_station.istake,
        'distance': nearest_station.distance
    }
    # res=model_to_dict(response_data)
    return JsonResponse(response_data,safe=False)
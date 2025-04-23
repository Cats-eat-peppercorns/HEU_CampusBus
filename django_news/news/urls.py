from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('isLogin/',views.isLogin,name='isLogin'),
    path('autoLogin/',views.autoLogin,name='autoLogin'),
    path('register/',views.register,name='register'),
    path('sendLocation/',views.sendLocation,name='sendLocation'),
    path('getLocation/',views.getLocation,name='getLocation'),
    path('duplicateLoginsTest/',views.duplicateLoginsTest,name='duplicateLoginsTest'),
    path('exitAccount/',views.exitAccount,name='exitAccount'),
    path('download_database/',views.download_database,name='download_database'),
    path('getLocationImage/',views.getLocationImage,name='getLocationImage'),
    
    path('driver_register/',views.driver_register,name='driver_register'),
    path('driver_login/',views.driver_login,name='driver_login'),
    path('driver_locationSend/',views.driver_locationSend,name='driver_locationSend'),
    path('driver_on_the_job/',views.driver_on_the_job,name='driver_on_the_job'),
    path('driver_get_workmates/',views.driver_get_workmates,name='driver_get_workmates'),
    path('driver_information_set/',views.driver_information_set,name='driver_information_set'),
    path('delete_driver_account/',views.delete_driver_account,name='delete_driver_account'),
    path('driver_getSignedDays/',views.driver_getSignedDays,name='driver_getSignedDays'),
    path('driver_send_clockIn_date/',views.driver_send_clockIn_date,name='driver_send_clockIn_date'),
    path('get_nearest_bus/',views.get_nearest_bus,name='get_nearest_bus'),
    path('get_nearest_station/',views.get_nearest_station,name='get_nearest_station'),
    #path('get_nearest_stationOff/',views.get_nearest_stationOff,name='get_nearest_stationOff'),
    

]

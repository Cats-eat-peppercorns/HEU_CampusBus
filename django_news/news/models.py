from django.db import models
class User(models.Model):
    id = models.BigAutoField(primary_key=True)
    name = models.CharField(unique=True, max_length=10)
    account = models.CharField(unique=True, max_length=16)
    password = models.CharField(max_length=16)
    avatar = models.TextField()
    islogin = models.IntegerField()
    session = models.ForeignKey('DjangoSession', models.DO_NOTHING, blank=True, null=True)
    class Meta:
        db_table = 'user'  # 指明数据库表名
class DjangoSession(models.Model):
    session_key = models.CharField(primary_key=True, max_length=40)
    session_data = models.TextField()
    expire_date = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'django_session'
class School(models.Model):
    school_id=models.AutoField(primary_key=True,verbose_name="id")
    school_name=models.CharField(max_length=100,unique=True,verbose_name='学校名')
    school_address=models.CharField(max_length=100,verbose_name='学校地址')
    school_latitude = models.DecimalField(null=True,max_digits=9, decimal_places=6,verbose_name="学校纬度")
    school_longitude = models.DecimalField(null=True,max_digits=9, decimal_places=6,verbose_name="学校经度")
    def __str__(self):
        return self.school_name
    class Meta:
        db_table='school'

class Bus_line(models.Model):
    line_id=models.AutoField(primary_key=True,verbose_name="id")
    line_name=models.CharField(max_length=100,verbose_name='线路名')
    line_school=models.ForeignKey(School,on_delete=models.CASCADE,verbose_name='所属学校')
    def __str__(self):
        return self.line_name
    class Meta:
        db_table='bus_line'

class Bus_station(models.Model):
    station_id=models.AutoField(primary_key=True,verbose_name="id")
    station_name=models.CharField(max_length=100,verbose_name='站点名')
    station_latitude = models.DecimalField(null=True,max_digits=9, decimal_places=6,verbose_name="纬度")
    station_longitude = models.DecimalField(null=True,max_digits=9, decimal_places=6,verbose_name="经度")
    station_school=models.ForeignKey(School,on_delete=models.CASCADE,verbose_name="所属学校") 
    station_line=models.ForeignKey(Bus_line,on_delete=models.CASCADE,verbose_name="所属线路")
    next_station=models.ForeignKey('self',on_delete=models.CASCADE,related_name='next_stations',null=True,verbose_name="下一站")
    last_station=models.ForeignKey('self',on_delete=models.CASCADE,related_name='previous_stations',null=True,verbose_name="上一站")
    def __str__(self):
        return self.station_name
    class Meta:
        db_table='bus_station'

class Driver(models.Model):
    driver_id=models.AutoField(primary_key=True,verbose_name="id")
    name=models.CharField(max_length=20,verbose_name="姓名")
    account=models.CharField(max_length=16,unique=True,verbose_name='司机账号')
    password=models.CharField(max_length=16,verbose_name='司机密码')
    phone=models.CharField(max_length=12,verbose_name='司机电话')
    driver_school=models.ForeignKey(School,on_delete=models.CASCADE,verbose_name="所属学校")
    driver_line=models.ForeignKey(Bus_line,on_delete=models.CASCADE,verbose_name="所属线路")
    def __str__(self):
        return self.name
    class Meta:
        db_table='driver'

class Driver_location(models.Model):
    location_id=models.AutoField(primary_key=True,verbose_name="id")
    location_driver=models.ForeignKey(Driver,on_delete=models.CASCADE,verbose_name="司机")
    latitude = models.DecimalField(null=True,max_digits=9, decimal_places=6,verbose_name="纬度")
    longitude = models.DecimalField(null=True,max_digits=9, decimal_places=6,verbose_name="经度")
    last_modified = models.DateTimeField(auto_now=True,verbose_name="经纬更新时间")  # 自动更新时间
    isworking = models.BooleanField(default=False,verbose_name="是否在岗")
    direction = models.BooleanField(default=False,verbose_name="行驶方向")
    next_station =models.ForeignKey(Bus_station,null=True,related_name='driver_next_station',on_delete=models.CASCADE,verbose_name="下一站点")
    now_station =models.ForeignKey(Bus_station,null=True,related_name='driver_now_station',on_delete=models.CASCADE,verbose_name="所在站点")
    def __str__(self):
        return self.name
    class Meta:
        db_table='driver_location'

class Driver_clockIn(models.Model):
    clock_in_id=models.AutoField(primary_key=True,verbose_name="id")
    clock_in_driver=models.ForeignKey(Driver,on_delete=models.CASCADE,verbose_name="司机")
    clock_in_date=models.DateField(auto_now=True,verbose_name="打卡时间")
    isclock=models.BooleanField(default=False,verbose_name="是否打卡")
    def __str__(self):
        return self.name
    class Meta:
        db_table='driver_clock_in'

class Driver_month_chockIn(models.Model):
    month_clockIn_id=models.AutoField(primary_key=True,verbose_name="id")
    month_clockIn_driver=models.ForeignKey(Driver,on_delete=models.CASCADE,verbose_name="司机")
    month_clockIn_date=models.DateField(auto_now=True,verbose_name="日期")
    month_clockIn_days=models.IntegerField(default=0,verbose_name="打卡天数")
    month_clockIn_lack_days=models.IntegerField(default=0,verbose_name="缺勤天数")
    month_clockIn_wage=models.IntegerField(default=0,verbose_name="当前工资")
    def __str__(self):
        return self.name
    class Meta:
        db_table='driver_month_clockIn'
class Location(models.Model):
    locationid = models.CharField(primary_key=True,db_column='locationId', max_length=255)  # Field name made lowercase.
    locationname = models.CharField(db_column='locationName', max_length=255, blank=True, null=True)  # Field name made lowercase.
    latitude = models.CharField(max_length=255, blank=True, null=True)
    longitude = models.CharField(max_length=255, blank=True, null=True)
    ivpath = models.CharField(db_column='IvPath', max_length=255, blank=True, null=True)  # Field name made lowercase.
    nearesttake = models.CharField(db_column='nearestTake', max_length=255, blank=True, null=True)  # Field name made lowercase.
    nearestoff = models.CharField(db_column='nearestOff', max_length=255, blank=True, null=True)  # Field name made lowercase.
    istake = models.CharField(db_column='isTake', max_length=1, db_collation='utf8mb4_bin', blank=True, null=True)  # Field name made lowercase.    

    class Meta:
        managed = False
        db_table = 'location'
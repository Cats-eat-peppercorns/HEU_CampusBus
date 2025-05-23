# Generated by Django 3.2.21 on 2024-03-27 05:08

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='DjangoSession',
            fields=[
                ('session_key', models.CharField(max_length=40, primary_key=True, serialize=False)),
                ('session_data', models.TextField()),
                ('expire_date', models.DateTimeField()),
            ],
            options={
                'db_table': 'django_session',
                'managed': False,
            },
        ),
        migrations.CreateModel(
            name='Location',
            fields=[
                ('locationid', models.CharField(db_column='locationId', max_length=255, primary_key=True, serialize=False)),
                ('locationname', models.CharField(blank=True, db_column='locationName', max_length=255, null=True)),
                ('latitude', models.CharField(blank=True, max_length=255, null=True)),
                ('longitude', models.CharField(blank=True, max_length=255, null=True)),
                ('ivpath', models.CharField(blank=True, db_column='IvPath', max_length=255, null=True)),
                ('nearesttake', models.CharField(blank=True, db_column='nearestTake', max_length=255, null=True)),
                ('nearestoff', models.CharField(blank=True, db_column='nearestOff', max_length=255, null=True)),
                ('istake', models.CharField(blank=True, db_collation='utf8mb4_bin', db_column='isTake', max_length=1, null=True)),
            ],
            options={
                'db_table': 'location',
                'managed': False,
            },
        ),
        migrations.CreateModel(
            name='Bus_line',
            fields=[
                ('line_id', models.AutoField(primary_key=True, serialize=False, verbose_name='id')),
                ('line_name', models.CharField(max_length=100, verbose_name='线路名')),
            ],
            options={
                'db_table': 'bus_line',
            },
        ),
        migrations.CreateModel(
            name='Bus_station',
            fields=[
                ('station_id', models.AutoField(primary_key=True, serialize=False, verbose_name='id')),
                ('station_name', models.CharField(max_length=100, verbose_name='站点名')),
                ('station_latitude', models.DecimalField(decimal_places=6, max_digits=9, null=True, verbose_name='纬度')),
                ('station_longitude', models.DecimalField(decimal_places=6, max_digits=9, null=True, verbose_name='经度')),
                ('last_station', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='previous_stations', to='news.bus_station', verbose_name='上一站')),
                ('next_station', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='next_stations', to='news.bus_station', verbose_name='下一站')),
                ('station_line', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.bus_line', verbose_name='所属线路')),
            ],
            options={
                'db_table': 'bus_station',
            },
        ),
        migrations.CreateModel(
            name='Driver',
            fields=[
                ('driver_id', models.AutoField(primary_key=True, serialize=False, verbose_name='id')),
                ('name', models.CharField(max_length=20, verbose_name='姓名')),
                ('account', models.CharField(max_length=16, unique=True, verbose_name='司机账号')),
                ('password', models.CharField(max_length=16, verbose_name='司机密码')),
                ('phone', models.CharField(max_length=12, verbose_name='司机电话')),
                ('driver_line', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.bus_line', verbose_name='所属线路')),
            ],
            options={
                'db_table': 'driver',
            },
        ),
        migrations.CreateModel(
            name='School',
            fields=[
                ('school_id', models.AutoField(primary_key=True, serialize=False, verbose_name='id')),
                ('school_name', models.CharField(max_length=100, unique=True, verbose_name='学校名')),
                ('school_address', models.CharField(max_length=100, verbose_name='学校地址')),
                ('school_latitude', models.DecimalField(decimal_places=6, max_digits=9, null=True, verbose_name='学校纬度')),
                ('school_longitude', models.DecimalField(decimal_places=6, max_digits=9, null=True, verbose_name='学校经度')),
            ],
            options={
                'db_table': 'school',
            },
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.BigAutoField(primary_key=True, serialize=False)),
                ('name', models.CharField(max_length=10, unique=True)),
                ('account', models.CharField(max_length=16, unique=True)),
                ('password', models.CharField(max_length=16)),
                ('avatar', models.TextField()),
                ('islogin', models.IntegerField()),
                ('session', models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.DO_NOTHING, to='news.djangosession')),
            ],
            options={
                'db_table': 'user',
            },
        ),
        migrations.CreateModel(
            name='Driver_month_chockIn',
            fields=[
                ('month_clockIn_id', models.AutoField(primary_key=True, serialize=False, verbose_name='id')),
                ('month_clockIn_date', models.DateField(auto_now=True, verbose_name='日期')),
                ('month_clockIn_days', models.IntegerField(default=0, verbose_name='打卡天数')),
                ('month_clockIn_lack_days', models.IntegerField(default=0, verbose_name='缺勤天数')),
                ('month_clockIn_wage', models.IntegerField(default=0, verbose_name='当前工资')),
                ('month_clockIn_driver', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.driver', verbose_name='司机')),
            ],
            options={
                'db_table': 'driver_month_clockIn',
            },
        ),
        migrations.CreateModel(
            name='Driver_location',
            fields=[
                ('location_id', models.AutoField(primary_key=True, serialize=False, verbose_name='id')),
                ('latitude', models.DecimalField(decimal_places=6, max_digits=9, null=True, verbose_name='纬度')),
                ('longitude', models.DecimalField(decimal_places=6, max_digits=9, null=True, verbose_name='经度')),
                ('last_modified', models.DateTimeField(auto_now=True, verbose_name='经纬更新时间')),
                ('isworking', models.BooleanField(default=False, verbose_name='是否在岗')),
                ('direction', models.BooleanField(default=False, verbose_name='行驶方向')),
                ('location_driver', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.driver', verbose_name='司机')),
                ('next_station', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='driver_next_station', to='news.bus_station', verbose_name='下一站点')),
                ('now_station', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='driver_now_station', to='news.bus_station', verbose_name='所在站点')),
            ],
            options={
                'db_table': 'driver_location',
            },
        ),
        migrations.CreateModel(
            name='Driver_clockIn',
            fields=[
                ('clock_in_id', models.AutoField(primary_key=True, serialize=False, verbose_name='id')),
                ('clock_in_date', models.DateField(auto_now=True, verbose_name='打卡时间')),
                ('isclock', models.BooleanField(default=False, verbose_name='是否打卡')),
                ('clock_in_driver', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.driver', verbose_name='司机')),
            ],
            options={
                'db_table': 'driver_clock_in',
            },
        ),
        migrations.AddField(
            model_name='driver',
            name='driver_school',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.school', verbose_name='所属学校'),
        ),
        migrations.AddField(
            model_name='bus_station',
            name='station_school',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.school', verbose_name='所属学校'),
        ),
        migrations.AddField(
            model_name='bus_line',
            name='line_school',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='news.school', verbose_name='所属学校'),
        ),
    ]

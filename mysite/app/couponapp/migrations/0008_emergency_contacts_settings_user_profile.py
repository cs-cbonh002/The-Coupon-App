# Generated by Django 5.0.3 on 2024-04-07 21:01

import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('couponapp', '0007_incident_log'),
    ]

    operations = [
        migrations.CreateModel(
            name='Emergency_Contacts',
            fields=[
                ('Emergency_Contact_ID', models.PositiveIntegerField(primary_key=True, serialize=False)),
                ('Counsel', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.counsel')),
                ('Person', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.person')),
                ('Shelter', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.shelter')),
            ],
        ),
        migrations.CreateModel(
            name='Settings',
            fields=[
                ('Settings_ID', models.PositiveIntegerField(primary_key=True, serialize=False)),
                ('wakewords_active', models.BooleanField(default=False)),
                ('audio_active', models.BooleanField(default=False)),
                ('video_active', models.BooleanField(default=False)),
                ('gpsSpoofing_active', models.BooleanField(default=False)),
                ('Incident_Log', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.incident_log')),
                ('Recording', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.recording')),
            ],
        ),
        migrations.CreateModel(
            name='User_Profile',
            fields=[
                ('UserProfile_ID', models.PositiveIntegerField(primary_key=True, serialize=False)),
                ('first_name', models.CharField(blank=True, max_length=30)),
                ('last_name', models.CharField(blank=True, max_length=30)),
                ('gender', models.CharField(blank=True, max_length=10)),
                ('age', models.IntegerField(default=0)),
                ('email', models.EmailField(max_length=30)),
                ('phone_number', models.IntegerField(default=0)),
                ('address', models.TextField(blank=True, max_length=100)),
                ('relationship', models.TextField(blank=True, max_length=50)),
                ('Emergency_Contacts', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.emergency_contacts')),
                ('Incident_Log', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.incident_log')),
                ('Settings', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.settings')),
                ('Users', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='couponapp.users')),
            ],
        ),
    ]

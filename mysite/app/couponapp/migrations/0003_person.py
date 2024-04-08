# Generated by Django 5.0.3 on 2024-04-07 20:35

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('couponapp', '0002_themes_users_themes'),
    ]

    operations = [
        migrations.CreateModel(
            name='Person',
            fields=[
                ('Person_ID', models.PositiveIntegerField(primary_key=True, serialize=False)),
                ('first_name', models.CharField(max_length=30)),
                ('last_name', models.CharField(max_length=30)),
                ('email', models.EmailField(max_length=30)),
                ('phone_number', models.IntegerField(default=0)),
                ('relationship', models.TextField(max_length=20)),
            ],
        ),
    ]
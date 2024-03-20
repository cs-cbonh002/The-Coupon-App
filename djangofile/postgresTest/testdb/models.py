from django.db import models
from django.contrib.auth.models import User
from django.conf import settings
import id


class Themes(models.Model):
    Themes_ID = models.PositiveIntegerField(primary_key=True)
    theme_name = models.CharField(max_length=30)
    theme_color = models.CharField(max_length=30)
    #font_family = models.EmailField()
    #Icon = models.TextField()
    
    def __str__(self):
        return self.theme_name

class Users(models.Model):
    User_ID = models.PositiveIntegerField(primary_key=True)
    Themes = models.ForeignKey(Themes, on_delete=models.CASCADE) #(`Themes` to `Users`) one to many
    username = models.TextField(max_length=30)  # Using CharField for username
    pin = models.CharField(max_length=30)

    def __str__(self):
        return self.username

class User_Profile(models.Model):
    UserProfile_ID = models.PositiveIntegerField(primary_key=True)
    Emergency_Contacts = models.ForeignKey(Emergency_Contacts, on_delete=models.CASCADE)
    Incident_Log = models.ForeignKey(Incident_Log, on_delete=models.CASCADE)
    Users = models.PositiveIntegerField(Users,on_delete=models.CASCADE) #(`Users` to `User_Profile`) one to many
    Settings = models.ForeignKey(Settings, on_delete=models.CASCADE)
    first_name = models.CharField(max_length=30)
    last_name = models.CharField(max_length=30)
    gender = models.CharField(max_length=10)  # Assuming gender can be 'Male', 'Female', 'Other'
    age = models.IntegerField(default=0)
    phone_number = models.IntegerField(default=0)
    email = models.EmailField(max_length=30)
    phone_number = models.IntegerField(default=0)
    address = models.TextField(max_length=100)
    relationship = models.TextField(max_length=50)

    def __str__(self):
        return f"{self.first_name} {self.last_name}"
    

class Settings(models.Model):
    Settings_ID = models.PositiveIntegerField(primary_key=True)
    Incident_Log = models.ForeignKey(Incident_Log, on_delete=models.CASCADE) #(`Settings` to `Incident_Log`) one to many
    Recording = models.ForeignKey(Recording, on_delete=models.CASCADE)
    wakewords_active = models.BooleanField(default=False)
    audio_active = models.BooleanField(default=False)
    video_active = models.BooleanField(default=False)
    gpsSpoofing_active = models.BooleanField(default=False)
    # wakewords_active=
    # wakewords_inactive =
    # audio_active =
    # audio_inactive =
    # video_active =
    # video_inactive =
    # gpsSpoofing_active =
    # gpsSpoofing_inactive =
    def __str__(self):
        return f"Settings ID: {self.Settings_ID}"


# (`GPS_Location` to `Emergency_Contacts` and `Shelter`) one to many
class Emergency_Contacts(models.Model):
    emergencyContact_ID = models.PositiveIntegerField(primary_key=True)
    Person = models.ForeignKey(Person, on_delete=models.CASCADE)
    Shelter = models.ForeignKey(Shelter, on_delete=models.CASCADE)
    Counsel = models.ForeignKey(Counsel, on_delete=models.CASCADE)
    
    def __str__(self):
        return f"Emergency Contact ID: {self.emergencyContact_ID}"


class Person(models.Model):
    Person_ID = models.PositiveIntegerField(primary_key=True)
    first_name = models.CharField(max_length=30)
    last_name = models.CharField(max_length=30)
    email = models.EmailField(max_length=30)
    phone_number = models.IntegerField(default=0) # Change to CharField for phone number?
    relationship = models.TextField(max_length=20)
    #relationship = models.CharField(max_length=30)

    def __str__(self):
        return (self.first_name + " "+ self.last_name)
    

class Shelter(models.Model):
    Shelter_ID = models.PositiveIntegerField(primary_key=True)
    first_name = models.CharField(max_length=30)
    last_name = models.CharField(max_length=30)
    phone_number = models.IntegerField(default=0)
    email = models.CharField(max_length=30) # EmailField for email addresses
    website = models.URLField(max_length=200)  # URLField for website URLs

    def __str__(self):
        return (self.first_name + " "+ self.last_name)

class Counsel(models.Model):
    Counsel_ID = models.PositiveIntegerField(primary_key=True)
    first_name = models.CharField(max_length=30)
    last_name = models.CharField(max_length=30)
    phone_number = models.IntegerField(default=0)
    address = models.TextField(max_length=30)
    email = models.EmailField(max_length=50)
    website = models.URLField(max_length=200)  # URLField for website URLs

    def __str__(self):
        return (self.first_name + " "+ self.last_name)

class GPS_Location(models.Model):
    GPS_ID = models.PositiveIntegerField(primary_key=True)
    Emergency_Contacts = models.ForeignKey(Emergency_Contacts, on_delete=models.CASCADE)
    Shelter = models.ForeignKey(Shelter, on_delete=models.CASCADE)
    latitude = models.FloatField(min_value=-90, max_value=90)# or (validators=[MinValueValidator(-90), MaxValueValidator(90)])
    longitude = models.FloatField(min_value=-180, max_value=180)# or (validators=[MinValueValidator(-180), MaxValueValidator(180)])

    def __str__(self):
        return f"GPS Location ID: {self.GPS_ID}"

class Incident_Log(models.Model):
    incidentLog_ID = models.PositiveIntegerField(primary_key=True)
    incident_date = models.DateField(max_length=30)
    incident_time = models.DateTimeField(max_length=30)
    description = models.CharField(max_length=100)
    
    def __str__(self):
        return f"Incident Log ID: {self.incidentLog_ID}"

class Recording(models.Model):
    Recording_ID = models.PositiveIntegerField(primary_key=True)
    audioSegment_number = models.IntegerField(default=0)
    videdoSegment_number = models.IntegerField(default=0)
    recording_date = models.DateField()
    audio_duration = models.DateTimeField()
    video_duration = models.DateTimeField()
    audioFile_size = models.FloatField()
    videoFile_size = models.FloatField()
    
    def __str__(self):
        return f"Recording ID: {self.Recording_ID}"


    




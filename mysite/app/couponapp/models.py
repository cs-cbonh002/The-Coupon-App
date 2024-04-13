from django.db import models

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
    username = models.TextField(max_length=30, blank= True)  # Using CharField for username
    pin = models.CharField(max_length=30)

    def __str__(self):
        return self.username
    

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
   # website = models.URLField(default=0, 'https://www.lawyer.com/')  # URLField for website URLs

    def __str__(self):
        return (self.first_name + " "+ self.last_name)

class Recording(models.Model):
    Recording_ID = models.PositiveIntegerField(primary_key=True)
    audioSegment_number = models.IntegerField(default=0)
    videdoSegment_number = models.IntegerField(default=0)
    recording_date = models.DateField() #save location on here but create path to native storage on phone
    audio_duration = models.DateTimeField()
    video_duration = models.DateTimeField()
    audioFile_size = models.FloatField()
    videoFile_size = models.FloatField()
    
    def __str__(self):
        return f"Recording ID: {self.Recording_ID}"
    
class Incident_Log(models.Model):
    incidentLog_ID = models.PositiveIntegerField(primary_key=True)
    incident_date = models.DateField(max_length=30)
    incident_time = models.DateTimeField(max_length=30)
    description = models.CharField(max_length=200, null= True)
    
    def __str__(self):
        return f"Incident Log ID: {self.incidentLog_ID}"
    

class Emergency_Contacts(models.Model):
    Emergency_Contact_ID = models.PositiveIntegerField(primary_key=True)
    Person = models.ForeignKey(Person, on_delete=models.CASCADE)
    Shelter = models.ForeignKey(Shelter, on_delete=models.CASCADE)
    Counsel = models.ForeignKey(Counsel, on_delete=models.CASCADE)

class Settings(models.Model):
    Settings_ID = models.PositiveIntegerField(primary_key=True)
    Incident_Log = models.ForeignKey(Incident_Log, on_delete=models.CASCADE) #(`Settings` to `Incident_Log`) one to many
    Recording = models.ForeignKey(Recording, on_delete=models.CASCADE)
    wakewords_active = models.BooleanField(default=False)
    audio_active = models.BooleanField(default=False)
    video_active = models.BooleanField(default=False)
    gpsSpoofing_active = models.BooleanField(default=False)
    
class User_Profile(models.Model):
    UserProfile_ID = models.PositiveIntegerField(primary_key=True)
    Emergency_Contacts = models.ForeignKey(Emergency_Contacts, on_delete=models.CASCADE)
    Incident_Log = models.ForeignKey(Incident_Log, on_delete=models.CASCADE)
    Users = models.ForeignKey(Users,on_delete=models.CASCADE) #(`Users` to `User_Profile`) one to many
    Settings = models.ForeignKey(Settings, on_delete=models.CASCADE)
    first_name = models.CharField(max_length=30, blank=True)
    last_name = models.CharField(max_length=30,blank=True)
    gender = models.CharField(max_length=10, blank=True)  # Assuming gender can be 'Male', 'Female', 'Other'
    age = models.IntegerField(default=0)
    phone_number = models.IntegerField(default=0)
    email = models.EmailField(max_length=30)
    phone_number = models.IntegerField(default=0)
    address = models.TextField(max_length=100, blank=True)
    relationship = models.TextField(max_length=50, blank=True)

    def __str__(self):
        return f"{self.first_name} {self.last_name}"
    



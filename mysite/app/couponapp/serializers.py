from rest_framework import serializers
from .models import Themes, Users, Person, User_Profile, Shelter, Settings, Counsel, Recording, Incident_Log
from rest_framework_simplejwt.views import TokenObtainPairView
from django.contrib.auth.models import User

from rest_framework import serializers

class Themes(serializers.Serializer):
    Themes_ID = serializers.PositiveIntegerField(primary_key=True)
    theme_name = serializers.CharField(max_length=30)
    theme_color = serializers.CharField(max_length=30)

    
    def __str__(self):
        return self.theme_name
    
    def create(self, validated_data):
        return Comment.objects.create(**validated_data)

    # def update(self, instance, validated_data):
    #     instance.email = validated_data.get('email', instance.email)
    #     instance.content = validated_data.get('content', instance.content)
    #     instance.created = validated_data.get('created', instance.created)
    #     instance.save()
    #     return instance

class Users(serializers.Serializer):
    User_ID = serializers.PositiveIntegerField(primary_key=True)
    Themes = serializers.ForeignKey(Themes, on_delete=serializers.CASCADE) #(`Themes` to `Users`) one to many
    username = serializers.TextField(max_length=30, blank= True)  # Using CharField for username
    pin = serializers.CharField(max_length=30)

    def __str__(self):
        return self.username
    

class Person(serializers.Serializer):
    Person_ID = serializers.PositiveIntegerField(primary_key=True)
    first_name = serializers.CharField(max_length=30)
    last_name = serializers.CharField(max_length=30)
    email = serializers.EmailField(max_length=30)
    phone_number = serializers.IntegerField(default=0) # Change to CharField for phone number?
    relationship = serializers.TextField(max_length=20)
    #relationship = serializers.CharField(max_length=30)

    def __str__(self):
        return (self.first_name + " "+ self.last_name)
    
class Shelter(serializers.Serializer):
    Shelter_ID = serializers.PositiveIntegerField(primary_key=True)
    first_name = serializers.CharField(max_length=30)
    last_name = serializers.CharField(max_length=30)
    phone_number = serializers.IntegerField(default=0)
    email = serializers.CharField(max_length=30) # EmailField for email addresses
    website = serializers.URLField(max_length=200)  # URLField for website URLs

    def __str__(self):
        return (self.first_name + " "+ self.last_name)

class Counsel(serializers.Serializer):
    Counsel_ID = serializers.PositiveIntegerField(primary_key=True)
    first_name = serializers.CharField(max_length=30)
    last_name = serializers.CharField(max_length=30)
    phone_number = serializers.IntegerField(default=0)
    address = serializers.TextField(max_length=30)
    email = serializers.EmailField(max_length=50)
   # website = serializers.URLField(default=0, 'https://www.lawyer.com/')  # URLField for website URLs

    def __str__(self):
        return (self.first_name + " "+ self.last_name)

class Recording(serializers.Serializer):
    Recording_ID = serializers.PositiveIntegerField(primary_key=True)
    audioSegment_number = serializers.IntegerField(default=0)
    videdoSegment_number = serializers.IntegerField(default=0)
    recording_date = serializers.DateField() #save location on here but create path to native storage on phone
    audio_duration = serializers.DateTimeField()
    video_duration = serializers.DateTimeField()
    audioFile_size = serializers.FloatField()
    videoFile_size = serializers.FloatField()
    
    def __str__(self):
        return f"Recording ID: {self.Recording_ID}"
    
class Incident_Log(serializers.Serializer):
    incidentLog_ID = serializers.PositiveIntegerField(primary_key=True)
    incident_date = serializers.DateField(max_length=30)
    incident_time = serializers.DateTimeField(max_length=30)
    description = serializers.CharField(max_length=200, null= True)
    
    def __str__(self):
        return f"Incident Log ID: {self.incidentLog_ID}"
    
# class IncidentLogSerializer(serializers.serializerserializer):
#     class Meta:
#         model = IncidentLog
#         fields = ['id', 'timestamp', 'duration', 'transcription', 'notes', 'user']
#         read_only_fields = ['user']

class Emergency_Contacts(serializers.Serializer):
    Emergency_Contact_ID = serializers.PositiveIntegerField(primary_key=True)
    Person = serializers.ForeignKey(Person, on_delete=serializers.CASCADE)
    Shelter = serializers.ForeignKey(Shelter, on_delete=serializers.CASCADE)
    Counsel = serializers.ForeignKey(Counsel, on_delete=serializers.CASCADE)

class Settings(serializers.Serializer):
    Settings_ID = serializers.PositiveIntegerField(primary_key=True)
    Incident_Log = serializers.ForeignKey(Incident_Log, on_delete=serializers.CASCADE) #(`Settings` to `Incident_Log`) one to many
    Recording = serializers.ForeignKey(Recording, on_delete=serializers.CASCADE)
    wakewords_active = serializers.BooleanField(default=False)
    audio_active = serializers.BooleanField(default=False)
    video_active = serializers.BooleanField(default=False)
    gpsSpoofing_active = serializers.BooleanField(default=False)
    
class User_Profile(serializers.Serializer):
    UserProfile_ID = serializers.PositiveIntegerField(primary_key=True)
    Emergency_Contacts = serializers.ForeignKey(Emergency_Contacts, on_delete=serializers.CASCADE)
    Incident_Log = serializers.ForeignKey(Incident_Log, on_delete=serializers.CASCADE)
    Users = serializers.ForeignKey(Users,on_delete=serializers.CASCADE) #(`Users` to `User_Profile`) one to many
    Settings = serializers.ForeignKey(Settings, on_delete=serializers.CASCADE)
    first_name = serializers.CharField(max_length=30, blank=True)
    last_name = serializers.CharField(max_length=30,blank=True)
    gender = serializers.CharField(max_length=10, blank=True)  # Assuming gender can be 'Male', 'Female', 'Other'
    age = serializers.IntegerField(default=0)
    phone_number = serializers.IntegerField(default=0)
    email = serializers.EmailField(max_length=30)
    phone_number = serializers.IntegerField(default=0)
    address = serializers.TextField(max_length=100, blank=True)
    relationship = serializers.TextField(max_length=50, blank=True)

    def __str__(self):
        return f"{self.first_name} {self.last_name}"
    



# class CategorySerializer(serializers.serializerserializer):
#     class Meta:
#         model = Category
#         fields = '__all__'

# class CouponSerializer(serializers.serializerserializer):
#     class Meta:
#         model = Coupon
#         fields = '__all__'

# class UserProfileSerializer(serializers.serializerserializer):
#     class Meta:
#         model = UserProfile
#         fields = ['address', 'pets', 'family_info', 'additional_info']

# class UserSerializer(serializers.serializerserializer):
#     profile = UserProfileSerializer(source='userprofile', read_only=True)
#     password = serializers.CharField(write_only=True, required=True, style={'input_type': 'password'})

#     class Meta:
#         model = User
#         fields = ('username', 'password','first_name', 'last_name', 'profile')

#     def create(self, validated_data):
#         user = User.objects.create_user(**validated_data)
#         return user

# class RegisterSerializer(serializers.serializerserializer):
#     class Meta:
#         model = User
#         fields = ('username', 'password')
#         extra_kwargs = {
#             'password': {'write_only': True},
#         }

#     def create(self, validated_data):
#         user = User.objects.create_user(
#             username=validated_data['username']
#             password=validated_data['password']
#         )
#         return user


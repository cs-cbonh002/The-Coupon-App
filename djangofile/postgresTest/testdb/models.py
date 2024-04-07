from django.db import models
from django.contrib.auth.models import User
from django.conf import settings


# class Themes(models.Model):
#     Themes_ID = models.PositiveIntegerField(primary_key=True)
#     theme_name = models.CharField(max_length=30)
#     theme_color = models.CharField(max_length=30)
#     #font_family = models.EmailField()
#     #Icon = models.TextField()
    
#     def __str__(self):
#         return self.theme_name

# class Users(models.Model):
#     User_ID = models.PositiveIntegerField(primary_key=True)
#     Themes = models.ForeignKey(Themes, on_delete=models.CASCADE) #(`Themes` to `Users`) one to many
#     username = models.TextField(max_length=30)  # Using CharField for username
#     pin = models.CharField(max_length=30)

#     def __str__(self):
#         return self.username

# class User_Profile(models.Model):
#     UserProfile_ID = models.PositiveIntegerField(primary_key=True)
#     Emergency_Contacts = models.ForeignKey(Emergency_Contacts, on_delete=models.CASCADE)
#     Incident_Log = models.ForeignKey(Incident_Log, on_delete=models.CASCADE)
#     Users = models.PositiveIntegerField(Users,on_delete=models.CASCADE) #(`Users` to `User_Profile`) one to many
#     Settings = models.ForeignKey(Settings, on_delete=models.CASCADE)
#     first_name = models.CharField(max_length=30, blank=True)
#     last_name = models.CharField(max_length=30,blank=True)
#     gender = models.CharField(max_length=10, blank=True)  # Assuming gender can be 'Male', 'Female', 'Other'
#     age = models.IntegerField(default=0)
#     phone_number = models.IntegerField(default=0)
#     email = models.EmailField(max_length=30)
#     phone_number = models.IntegerField(default=0)
#     address = models.TextField(max_length=100, blank=True)
#     relationship = models.TextField(max_length=50, blank=True)

#     def __str__(self):
#         return f"{self.first_name} {self.last_name}"


# class Settings(models.Model):
#     Settings_ID = models.PositiveIntegerField(primary_key=True)
#     Incident_Log = models.ForeignKey(Incident_Log, on_delete=models.CASCADE) #(`Settings` to `Incident_Log`) one to many
#     Recording = models.ForeignKey(Recording, on_delete=models.CASCADE)
#     wakewords_active = models.BooleanField(default=False)
#     audio_active = models.BooleanField(default=False)
#     video_active = models.BooleanField(default=False)
#     gpsSpoofing_active = models.BooleanField(default=False)
#     # wakewords_active=
#     # wakewords_inactive =
#     # audio_active =
#     # audio_inactive =
#     # video_active =
#     # video_inactive =
#     # gpsSpoofing_active =
#     # gpsSpoofing_inactive =
#     def __str__(self):
#         return f"Settings ID: {self.Settings_ID}"


# # (`GPS_Location` to `Emergency_Contacts` and `Shelter`) one to many
# class Emergency_Contacts(models.Model):
#     emergencyContact_ID = models.PositiveIntegerField(primary_key=True)
#     Person = models.ForeignKey(Person, on_delete=models.CASCADE)
#     Shelter = models.ForeignKey(Shelter, on_delete=models.CASCADE)
#     Counsel = models.ForeignKey(Counsel, on_delete=models.CASCADE)
    
#     def __str__(self):
#         return f"Emergency Contact ID: {self.emergencyContact_ID}"


# class Person(models.Model):
#     Person_ID = models.PositiveIntegerField(primary_key=True)
#     first_name = models.CharField(max_length=30)
#     last_name = models.CharField(max_length=30)
#     email = models.EmailField(max_length=30)
#     phone_number = models.IntegerField(default=0) # Change to CharField for phone number?
#     relationship = models.TextField(max_length=20)
#     #relationship = models.CharField(max_length=30)

#     def __str__(self):
#         return (self.first_name + " "+ self.last_name)
    

# class Shelter(models.Model):
#     Shelter_ID = models.PositiveIntegerField(primary_key=True)
#     first_name = models.CharField(max_length=30)
#     last_name = models.CharField(max_length=30)
#     phone_number = models.IntegerField(default=0)
#     email = models.CharField(max_length=30) # EmailField for email addresses
#     website = models.URLField(max_length=200)  # URLField for website URLs

#     def __str__(self):
#         return (self.first_name + " "+ self.last_name)

# class Counsel(models.Model):
#     Counsel_ID = models.PositiveIntegerField(primary_key=True)
#     first_name = models.CharField(max_length=30)
#     last_name = models.CharField(max_length=30)
#     phone_number = models.IntegerField(default=0)
#     address = models.TextField(max_length=30)
#     email = models.EmailField(max_length=50)
#     website = models.URLField(max_length=200)  # URLField for website URLs

#     def __str__(self):
#         return (self.first_name + " "+ self.last_name)

# class GPS_Location(models.Model):
#     GPS_ID = models.PositiveIntegerField(primary_key=True)
#     Emergency_Contacts = models.ForeignKey(Emergency_Contacts, on_delete=models.CASCADE)
#     Shelter = models.ForeignKey(Shelter, on_delete=models.CASCADE)
#     latitude = models.FloatField(min_value=-90, max_value=90)# or (validators=[MinValueValidator(-90), MaxValueValidator(90)])
#     longitude = models.FloatField(min_value=-180, max_value=180)# or (validators=[MinValueValidator(-180), MaxValueValidator(180)])

#     def __str__(self):
#         return f"GPS Location ID: {self.GPS_ID}"

# class Incident_Log(models.Model):
#     incidentLog_ID = models.PositiveIntegerField(primary_key=True)
#     incident_date = models.DateField(max_length=30)
#     incident_time = models.DateTimeField(max_length=30)
#     description = models.CharField(blank=True, null= True)
    
#     def __str__(self):
#         return f"Incident Log ID: {self.incidentLog_ID}"


# class Recording(models.Model):
#     Recording_ID = models.PositiveIntegerField(primary_key=True)
#     audioSegment_number = models.IntegerField(default=0)
#     videdoSegment_number = models.IntegerField(default=0)
#     recording_date = models.DateField()
#     audio_duration = models.DateTimeField()
#     video_duration = models.DateTimeField()
#     audioFile_size = models.FloatField()
#     videoFile_size = models.FloatField()
    
#     def __str__(self):
#         return f"Recording ID: {self.Recording_ID}"


    




# CREATE TABLE `emergency_contacts`(
#     `emergencyContact_ID` INT NOT NULL,
#     `Person_ID` VARCHAR(255) NOT NULL,
#     `shelter_id` INT NOT NULL,
#     `counsel_id` INT NOT NULL,
#     PRIMARY KEY(`emergencyContact_ID`)
# );
# CREATE TABLE `Themes`(
#     `themes_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
#     `theme_name` BIGINT NOT NULL,
#     `theme_color` VARCHAR(255) NOT NULL,
#     `font_family` DATE NOT NULL,
#     `title_page` INT NOT NULL
# );
# CREATE TABLE `gps_location`(
#     `gps_id` INT NOT NULL,
#     `latitude` DOUBLE(8, 2) NOT NULL,
#     `longitude` DOUBLE(8, 2) NOT NULL,
#     `timestamp` TIMESTAMP NOT NULL,
#     `emergencyContact_ID` INT NOT NULL,
#     `shelter_id` INT NOT NULL,
#     PRIMARY KEY(`gps_id`)
# );
# CREATE TABLE `local_resources_counsel`(
#     `counsel_id` INT NOT NULL,
#     `first_name` VARCHAR(255) NOT NULL,
#     `last_name` VARCHAR(255) NOT NULL,
#     `phone_number` VARCHAR(255) NOT NULL,
#     `email` VARCHAR(255) NOT NULL,
#     `website` INT NOT NULL,
#     `adress` INT NOT NULL
# );
# ALTER TABLE
#     `local_resources_counsel` ADD INDEX `local_resources_counsel_counsel_id_index`(`counsel_id`);
# CREATE TABLE `user_profile`(
#     `user_profileID` INT NOT NULL,
#     `user_id` INT NOT NULL,
#     `first_name` VARCHAR(255) NOT NULL,
#     `last_name` VARCHAR(255) NOT NULL,
#     `age` INT NOT NULL,
#     `contact` VARCHAR(255) NOT NULL,
#     `email` VARCHAR(255) NOT NULL,
#     `address` VARCHAR(255) NOT NULL,
#     `settings_id` INT NOT NULL,
#     `emergencyContact_id` INT NOT NULL,
#     `incident_id` INT NOT NULL,
#     `gender` INT NOT NULL,
#     `password` INT NOT NULL
# );
# ALTER TABLE
#     `user_profile` ADD INDEX `user_profile_user_profileid_index`(`user_profileID`);
# ALTER TABLE
#     `user_profile` ADD INDEX `user_profile_user_id_index`(`user_id`);
# CREATE TABLE `users`(
#     `user_id` INT NOT NULL,
#     `username` VARCHAR(255) NOT NULL,
#     `pin_number` INT NOT NULL,
#     `themes_id` INT NOT NULL,
#     PRIMARY KEY(`user_id`)
# );
# CREATE TABLE `Person`(
#     `Person_ID` INT NOT NULL,
#     `first_name` INT NOT NULL,
#     `last_name` INT NOT NULL,
#     `phone_number` INT NOT NULL,
#     `email` INT NOT NULL,
#     `relationship` INT NOT NULL
# );
# CREATE TABLE `local_resources_shelter`(
#     `shelter_id` INT NOT NULL,
#     `shelter_name` VARCHAR(255) NOT NULL,
#     `shelter_address` VARCHAR(255) NOT NULL,
#     `phone_number` VARCHAR(255) NOT NULL,
#     `email` VARCHAR(255) NOT NULL,
#     `website` TINYINT(1) NOT NULL,
#     PRIMARY KEY(`shelter_id`)
# );
# CREATE TABLE `incident_log`(
#     `incident_id` INT NOT NULL,
#     `incident_date` DATE NOT NULL,
#     `incident_time` DATETIME NOT NULL,
#     `description` VARCHAR(255) NOT NULL,
#     PRIMARY KEY(`incident_id`)
# );
# CREATE TABLE `recording`(
#     `recodring_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
#     `videoSegment_number` INT NOT NULL,
#     `audio_duration` TIMESTAMP NOT NULL,
#     `video_duration` TIMESTAMP NOT NULL,
#     `date` DATE NOT NULL,
#     `audioFile_size` INT NOT NULL,
#     `videoFile_size` INT NOT NULL,
#     `audioSegment_number` INT NOT NULL
# );
# CREATE TABLE `settings`(
#     `settings_id` INT NOT NULL,
#     `incident_id` INT NOT NULL,
#     `wake words_enabled` VARCHAR(255) NOT NULL,
#     `recording_id` INT NOT NULL,
#     `video_enabled` VARCHAR(255) NOT NULL,
#     `audio_enabled` INT NOT NULL,
#     `gpsSpoofing_enabled` INT NOT NULL,
#     PRIMARY KEY(`settings_id`)
# );
# ALTER TABLE
#     `gps_location` ADD CONSTRAINT `gps_location_emergencycontact_id_foreign` FOREIGN KEY(`emergencyContact_ID`) REFERENCES `emergency_contacts`(`emergencyContact_ID`);
# ALTER TABLE
#     `users` ADD CONSTRAINT `users_themes_id_foreign` FOREIGN KEY(`themes_id`) REFERENCES `Themes`(`themes_id`);
# ALTER TABLE
#     `emergency_contacts` ADD CONSTRAINT `emergency_contacts_shelter_id_foreign` FOREIGN KEY(`shelter_id`) REFERENCES `local_resources_shelter`(`shelter_id`);
# ALTER TABLE
#     `emergency_contacts` ADD CONSTRAINT `emergency_contacts_person_id_foreign` FOREIGN KEY(`Person_ID`) REFERENCES `Person`(`Person_ID`);
# ALTER TABLE
#     `emergency_contacts` ADD CONSTRAINT `emergency_contacts_counsel_id_foreign` FOREIGN KEY(`counsel_id`) REFERENCES `local_resources_counsel`(`counsel_id`);
# ALTER TABLE
#     `user_profile` ADD CONSTRAINT `user_profile_emergencycontact_id_foreign` FOREIGN KEY(`emergencyContact_id`) REFERENCES `emergency_contacts`(`emergencyContact_ID`);
# ALTER TABLE
#     `user_profile` ADD CONSTRAINT `user_profile_settings_id_foreign` FOREIGN KEY(`settings_id`) REFERENCES `settings`(`settings_id`);
# ALTER TABLE
#     `settings` ADD CONSTRAINT `settings_incident_id_foreign` FOREIGN KEY(`incident_id`) REFERENCES `incident_log`(`incident_id`);
# ALTER TABLE
#     `settings` ADD CONSTRAINT `settings_recording_id_foreign` FOREIGN KEY(`recording_id`) REFERENCES `recording`(`recodring_id`);
# ALTER TABLE
#     `user_profile` ADD CONSTRAINT `user_profile_incident_id_foreign` FOREIGN KEY(`incident_id`) REFERENCES `incident_log`(`incident_id`);
# ALTER TABLE
#     `user_profile` ADD CONSTRAINT `user_profile_user_id_foreign` FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`);
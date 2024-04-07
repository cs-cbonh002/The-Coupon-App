from django.contrib import admin

from .models import Users
from .models import Themes
from .models import User_Profile
from .models import Emergency_Contacts
from .models import Person
from .models import Shelter
from .models import Counsel
# from .models import GPS_Location
from .models import Incident_Log
from .models import Recording
from .models import Settings

# Register your models here.

admin.site.register(Users)
admin.site.register(Themes)
admin.site.register(User_Profile)
admin.site.register(Emergency_Contacts)
admin.site.register(Person)
admin.site.register(Shelter)
admin.site.register(Counsel)
# admin.site.register(GPS_Location)
admin.site.register(Incident_Log)
admin.site.register(Recording)
admin.site.register(Settings)
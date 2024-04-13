# from rest_framework import serializers
# from .models import Category, Coupon, UserProfile, IncidentLog
# from rest_framework_simplejwt.views import TokenObtainPairView
# from django.contrib.auth.models import User


# class CategorySerializer(serializers.ModelSerializer):
#     class Meta:
#         model = Category
#         fields = '__all__'


# class CouponSerializer(serializers.ModelSerializer):
#     class Meta:
#         model = Coupon
#         fields = '__all__'


# class UserProfileSerializer(serializers.ModelSerializer):
#     class Meta:
#         model = UserProfile
#         fields = ['address', 'pets', 'family_info', 'additional_info']


# class UserSerializer(serializers.ModelSerializer):
#     profile = UserProfileSerializer(source='userprofile', read_only=True)
#     password = serializers.CharField(write_only=True, required=True, style={'input_type': 'password'})

#     class Meta:
#         model = User
#         fields = ('username', 'password','first_name', 'last_name', 'profile')

#     def create(self, validated_data):
#         user = User.objects.create_user(**validated_data)
#         return user

# class RegisterSerializer(serializers.ModelSerializer):
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

# class IncidentLogSerializer(serializers.ModelSerializer):
#     class Meta:
#         model = IncidentLog
#         fields = ['id', 'timestamp', 'duration', 'transcription', 'notes', 'user']
#         read_only_fields = ['user']


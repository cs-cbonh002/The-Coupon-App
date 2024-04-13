"""
URL configuration for app project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/5.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path

urlpatterns = [
    path('admin/', admin.site.urls),
]


# from django.contrib import admin
# from django.urls import path, include, reverse_lazy
# from django.contrib.auth.views import LogoutView
# from rest_framework.routers import DefaultRouter
# from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView
# from app.views import (
#     CustomLoginView,
#     RegisterView,
#     HomePageView,
#     CategoryViewSet,
#     CouponViewSet,
#     UserViewSet,
#     CurrentUserProfileView,
#     CurrentUserIncidentLogsView,
#     IncidentLogViewSet,
#     RegisterViewAPI,  # Make sure this is imported
# )

# # Setting up the router for Django REST Framework viewsets
# router = DefaultRouter()
# router.register(r'categories', CategoryViewSet)
# router.register(r'coupons', CouponViewSet)
# router.register(r'users', UserViewSet)

# # Define API patterns separately to avoid confusion and ensure clean organization
# api_patterns = [
#     path('token/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
#     path('token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
#     path('register/', RegisterViewAPI.as_view(), name='api_register'),  # API register endpoint
#     path('users/<int:user_pk>/incident_logs/', IncidentLogViewSet.as_view({'get': 'list', 'post': 'create'}), name='user-incident-logs-list'),
#     path('users/<int:user_pk>/incident_logs/<int:pk>/', IncidentLogViewSet.as_view({'get': 'retrieve', 'put': 'update', 'patch': 'update', 'delete': 'destroy'}), name='user-incident-logs-detail'),
#     path('users/me/', CurrentUserProfileView.as_view(), name='current-user-profile'),
#     path('users/me/incident_logs/', CurrentUserIncidentLogsView.as_view(), name='current-user-incident-logs'),
#     # Include DRF router URLs
#     path('', include(router.urls)),
# ]

# urlpatterns = [
#     path('', HomePageView.as_view(), name='home'),
#     path('admin/', admin.site.urls),
#     path('login/', CustomLoginView.as_view(), name='login'),
#     path('logout/', LogoutView.as_view(next_page=reverse_lazy('login')), name='logout'),
#     path('register/', RegisterView.as_view(), name='register'),  # Web register endpoint
#     # Prefix all API endpoints with 'api/'
#     path('api/', include((api_patterns, 'app'), namespace='api')),
# ]

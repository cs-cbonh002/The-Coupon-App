from django.contrib.auth import login
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.mixins import LoginRequiredMixin
from django.contrib.auth.views import LoginView
from django.http import HttpResponse
from django.shortcuts import redirect, render
from django.urls import reverse_lazy
from django.views.decorators.csrf import csrf_exempt
from django.views import View
from django.views.generic import TemplateView

from rest_framework import permissions, viewsets, status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.generics import RetrieveUpdateAPIView, ListCreateAPIView
from rest_framework.permissions import AllowAny

from .models import Category, Coupon, IncidentLog, User
from .serializers import (CategorySerializer, CouponSerializer, IncidentLogSerializer,
                UserProfileSerializer, UserSerializer, RegisterSerializer)

# Authentication & User Management Views

class CustomLoginView(LoginView):
    template_name = 'login.html'
    redirect_authenticated_user = True

    def get_success_url(self):
        return reverse_lazy('home')

class RegisterView(View):
    form_class = UserCreationForm
    template_name = 'register.html'
    partial_template_name = 'partials/registration_form.html'

    def get(self, request, *args, **kwargs):
        if request.user.is_authenticated:
            return redirect('home')
        form = self.form_class()
        context = {'form': form}
        template = self.partial_template_name if 'HX-Request' in request.headers else self.template_name
        return render(request, template, context)

    def post(self, request, *args, **kwargs):
        form = self.form_class(request.POST)
        if form.is_valid():
            user = form.save()
            login(request, user)
            response = HttpResponse('')
            # Use HX-Redirect for HTMX requests
            response['HX-Redirect'] = self.get_success_url()
            return response
        else:
            context = {'form': form}
            template = self.partial_template_name if 'HX-Request' in request.headers else self.template_name
            return render(request, template, context)

    def get_success_url(self):
        return reverse_lazy('home')

class RegisterViewAPI(APIView):
    permission_classes = [AllowAny]

    def post(self, request, *args, **kwargs):
        serializer = RegisterSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            return Response({
                "user": serializer.data,
                "message": "User created successfully."
            }, status=status.HTTP_201_CREATED)  # Use the status constant here
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class HomePageView(LoginRequiredMixin, TemplateView):
    template_name = 'home.html'
    login_url = reverse_lazy('login')

class CurrentUserProfileView(RetrieveUpdateAPIView):
    serializer_class = UserProfileSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_object(self):
        return self.request.user.userprofile

class CurrentUserIncidentLogsView(ListCreateAPIView):
    serializer_class = IncidentLogSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return IncidentLog.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAdminUser]

class CategoryViewSet(viewsets.ModelViewSet):
    queryset = Category.objects.all()
    serializer_class = CategorySerializer

class CouponViewSet(viewsets.ModelViewSet):
    queryset = Coupon.objects.all()
    serializer_class = CouponSerializer

class IncidentLogViewSet(viewsets.ModelViewSet):
    serializer_class = IncidentLogSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return IncidentLog.objects.filter(user=self.request.user)

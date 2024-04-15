package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class WebViewFragment extends Fragment {

    private WebView webView;

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = rootView.findViewById(R.id.webView);

        // Get the search query passed from the arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("searchQuery")) {
            String searchQuery = args.getString("searchQuery");
            if (searchQuery != null && !searchQuery.isEmpty()) {
                // Load the search URL into the WebView
                String searchUrl = "https://www.google.com/search?q=" + Uri.encode(searchQuery);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(searchUrl);
            }
        }

        return rootView;
    }
}


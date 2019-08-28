package io.github.kelvao.cloudflarescrapeexample;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;



public class MainActivity extends AppCompatActivity{

    public static final String URL = "https://codepen.io/jobs/";
    @BindView(R.id.rv_jobs)
    RecyclerView rv_jobs;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srl_refresh;
    private HashMap<String, String> cookies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new Connection().execute();

    }


    public class Connection extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... arg0){
            System.out.println("//////////////////////////////////////////////Working Directory = " +
                    System.getProperty("user.dir"));

            /*File pathBinary = new File("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
            FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);
            DesiredCapabilities desired = DesiredCapabilities.firefox();
            FirefoxOptions options = new FirefoxOptions();
            desired.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options.setBinary(firefoxBinary));*/
            // System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver_win32\\chromedriver.exe");
           System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver_win32\\chromedriver.exe");
            //System.setProperty("webdriver.gecko.driver","C:\\geckodriver.exe");
            WebDriver driver = new ChromeDriver();
            driver.get("http://www.wikipedia.org");

            WebElement link;
            link = driver.findElement(By.linkText("English"));
            link.click();

            return null;
        }
    }

}

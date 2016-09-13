package space.liberion.meetandsports.main;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import space.liberion.meetandsports.R;
import space.liberion.meetandsports.appointment.AppointmentFragment;
import space.liberion.meetandsports.booking.BookFragment;
import space.liberion.meetandsports.settings.SettingsFragment;
import space.liberion.meetandsports.ticket.TicketFragment;
import space.liberion.meetandsports.widgets.ImageTextButton;


public class MainActivity extends AppCompatActivity {

    static class MainActivityPagerAdapter extends FragmentPagerAdapter {

        List<BaseFragment> fragmentList;

        public MainActivityPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private ViewPager viewPager;
    private List<ImageTextButton> buttonList;
    private List<BaseFragment> fragmentList;
    private BookFragment bookFragment;
    private AppointmentFragment appointmentFragment;
    private TicketFragment ticketFragment;
    private SettingsFragment settingsFragment;

    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();
        initControls();
    }

    private void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.actionbar_main);
            actionBar.setElevation(10);
        }
    }

    private void initControls() {
        viewPager = (ViewPager) findViewById(R.id.main_pager);
        fragmentList = new ArrayList<>();
        buttonList = new ArrayList<>();

        buttonList.add((ImageTextButton) findViewById(R.id.book_btn));
        buttonList.add((ImageTextButton) findViewById(R.id.appointment_btn));
        buttonList.add((ImageTextButton) findViewById(R.id.ticket_btn));
        buttonList.add((ImageTextButton) findViewById(R.id.settings_btn));

        bookFragment = new BookFragment();
        appointmentFragment = new AppointmentFragment();
        ticketFragment = new TicketFragment();
        settingsFragment = new SettingsFragment();

        fragmentList.add(bookFragment);
        fragmentList.add(appointmentFragment);
        fragmentList.add(ticketFragment);
        fragmentList.add(settingsFragment);

        viewPager.setAdapter(new MainActivityPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                buttonList.get(currentItem).setChosen(false);
                buttonList.get(position).setChosen(true);
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void changeFragment(View view) {
        for (int i = 0; i < buttonList.size(); i ++) {
            if (view == buttonList.get(i)) {
                if (i == currentItem)
                    return;
                buttonList.get(currentItem).setChosen(false);
                buttonList.get(i).setChosen(true);
                viewPager.setCurrentItem(i);
                currentItem = i;
                return;
            }
        }
    }
}

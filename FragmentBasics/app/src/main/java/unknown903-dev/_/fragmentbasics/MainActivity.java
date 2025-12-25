package unknown903-dev._.fragmentbasics;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity
        implements HeadlineFragment.OnHeadlineSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        boolean isTwoPane = (fm.findFragmentById(R.id.news_fragment) != null);

        if (!isTwoPane){
            if (savedInstanceState == null){
                fm.beginTransaction()
                        .replace(R.id.fragment_container, new HeadlineFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onArticleSelected(int position) {
        FragmentManager fm = getSupportFragmentManager();

        // if exists, we're in two-pane (landscape)
        NewsFragment detail = (NewsFragment) fm.findFragmentById(R.id.news_fragment);

        if (detail != null) {
            // land update existing pane in place.
            detail.updateArticleView(position);
        } else {
            // portr swap container to show the detail fragment
            NewsFragment fragment = new NewsFragment();
            Bundle args = new Bundle();
            args.putInt(NewsFragment.ARG_POSITION, position);
            fragment.setArguments(args);

            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Back returns to the headlines list
                    .commit();
        }
    }
}

package alex_j.lab1a.fragmentbasics;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


    // TODO: Rename and change types and number of parameters
public class NewsFragment extends Fragment {
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            //inflate layout for this fragment
            return inflater.inflate(R.layout.fragment_news, container, false);
        }

        @Override
        public void onStart(){
            super.onStart();
            //on startup check if there are argument passed to the fragment
            Bundle args = getArguments();
            if(args != null){
                updateArticleView(args.getInt(ARG_POSITION));
            }
            else if(mCurrentPosition != -1){
                //set article based on saved instance state defined during onCreateview
                updateArticleView(mCurrentPosition);
            }
        }

        public void updateArticleView(int position) {
            TextView article = (TextView) getActivity().findViewById(R.id.news);
            article.setText(Ipsum.Articles[position]);
            mCurrentPosition = position;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            //save the current article selection in case we need to recreate the fragment
            outState.putInt(ARG_POSITION, mCurrentPosition);
        }
    }
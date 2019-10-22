package june.second.lunchmatchmaker.Etc;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import june.second.lunchmatchmaker.R;


public class CheckableLinearLayout extends LinearLayout implements Checkable {


    public CheckableLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        Log.i("del_check", " setchcecckd");


//        CheckBox cb = findViewById(R.id.principle_gallery_cb);
//
//        if (cb.isChecked() != checked){
//            cb.setChecked(checked);
//        }

    }

    @Override
    public boolean isChecked() {

        CheckBox cb = findViewById(R.id.principle_gallery_cb);
        Log.i("del_check", " ischcecckd");

        return cb.isChecked();

    }

    @Override
    public void toggle() {
        Log.i("del_check", " toggle");
        CheckBox cb = findViewById(R.id.principle_gallery_cb);
        setChecked(cb.isChecked() ? false : true);

    }
}

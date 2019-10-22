package june.second.lunchmatchmaker.View;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SmallRecyclerViewHolder extends RecyclerView.ViewHolder{
    private final ImageView imageView;


    //, AdapterView.OnItemClickListener clickListener
    public SmallRecyclerViewHolder(@NonNull ImageView itemView) {
        super(itemView);
        imageView = itemView;



    }

//
//    public void setImageResource(int imageResource) {
//        imageView.setImageResource(imageResource);
//    }

    public void setImageResource(Bitmap bitmap) {
//        imageView.setImageResource(imageResource);
        imageView.setImageBitmap(bitmap);
    }
}

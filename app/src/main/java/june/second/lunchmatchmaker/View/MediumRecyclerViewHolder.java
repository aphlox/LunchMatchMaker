package june.second.lunchmatchmaker.View;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MediumRecyclerViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;

    public MediumRecyclerViewHolder(@NonNull ImageView itemView) {
        super(itemView);
        imageView = itemView;
    }

    public void setImageResource(Bitmap bitmap) {
//        imageView.setImageResource(imageResource);
        imageView.setImageBitmap(bitmap);
    }

//    public void setContent(String string) {
//
//        imageView.setImageBitmap(bitmap);
//    }

}

package com.example.dsfrontendproject;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import java.util.List;
import com.squareup.picasso.Picasso;


public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> rooms;
    private bookingClickListener listener;


    public RoomsAdapter(Context context, List<Room> rooms, bookingClickListener listener) {
        this.context = context;
        this.rooms = rooms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(LayoutInflater.from(context).inflate(R.layout.display_room, parent, false));
    }


    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public interface bookingClickListener {
        void onBookingClick(Room room);
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage;
        TextView roomName,area,price,owner,numOfReviews;
        RatingBar rating;
        Button bookButton;

        public RoomViewHolder(View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomName = itemView.findViewById(R.id.roomName);
            area = itemView.findViewById(R.id.area);
            price = itemView.findViewById(R.id.price);
            owner = itemView.findViewById(R.id.owner);
            numOfReviews = itemView.findViewById(R.id.numOfReviews);
            rating = itemView.findViewById(R.id.rating);
            bookButton = itemView.findViewById(R.id.bookButton);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.roomImage.setImageBitmap(room.getImageBitmap());
        //Picasso.get().load(room.getImage()).into(holder.roomImage);
        holder.roomName.setText(room.getRoomName());
        holder.area.setText("Area: " + room.getArea(room));
        holder.price.setText("Price: " + String.valueOf(room.getPrice()) );
        holder.owner.setText("Owner: " + room.getOwner(room));
        holder.numOfReviews.setText("Reviews: " + String.valueOf(room.getNoReviews()));
        holder.rating.setRating((float) room.getStars());

        holder.bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBookingClick(room);
                }
            }
        });

    }

    // Method to update room image
    public void updateRoomImage(String roomName, Bitmap bitmap) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.getRoomName().equals(roomName)) {
                room.setImageBitmap(bitmap);
                notifyItemChanged(i); // Update only the specific item
                break;
            }
        }
    }

}


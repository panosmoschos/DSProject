package com.example.dsfrontendproject;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import com.example.dsfrontendproject.roomClasses.Room;
import java.util.List;
import com.squareup.picasso.Picasso;


public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> rooms;

    public RoomsAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(LayoutInflater.from(context).inflate(R.layout.display_room, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);

        Picasso.get().load(room.getImage()).into(holder.roomImage);
        holder.roomName.setText(room.getRoomName());
        holder.area.setText(room.getArea(room));
        holder.price.setText(room.getPrice());
        holder.owner.setText(room.getOwner(room));
        holder.rating.setRating((float) room.getStars());
        //holder.bookButton.setOnClickListener(v -> onBookClickListener.onBookClick(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage;
        TextView roomName,area,price,owner,numOfReviews;
        RatingBar rating;
        Button bookButton;

        public RoomViewHolder(View roomView) {
            super(roomView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomName = itemView.findViewById(R.id.roomName);
            area = itemView.findViewById(R.id.area);
            price = itemView.findViewById(R.id.price);
            owner = itemView.findViewById(R.id.owner);
            numOfReviews = itemView.findViewById(R.id.numOfReviews);
            rating = itemView.findViewById(R.id.rating);
            bookButton = itemView.findViewById(R.id.bookButton);

            // thelei ylopoihshhhh
            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("booked");//kane to booking
                }
            });
        }

    }

}


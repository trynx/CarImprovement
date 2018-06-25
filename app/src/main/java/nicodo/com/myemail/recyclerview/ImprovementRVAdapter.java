package nicodo.com.myemail.recyclerview;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import nicodo.com.myemail.Improvement;
import nicodo.com.myemail.R;

public class ImprovementRVAdapter  extends RecyclerView.Adapter<ImprovementRVAdapter.ImprovementViewHolder> {


    public List<Improvement> improvements;

    private EditText unFocuseText;

    // Constructor
    public ImprovementRVAdapter(List<Improvement> list, EditText unFocuseText){
        improvements = list;
        this.unFocuseText = unFocuseText;
    }


    public class ImprovementViewHolder extends RecyclerView.ViewHolder{

        CardView cvImprovement;
        TextView tvImprovement;



        public ImprovementViewHolder(View itemView) {
            super(itemView);

            cvImprovement = (CardView) itemView.findViewById(R.id.cv_improvement);
            tvImprovement = (TextView) itemView.findViewById(R.id.name_improvement);
        }

    }

    @Override
    public ImprovementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.improvement_item, parent, false);
        return new ImprovementViewHolder(view);
    }

    // Which item to show at each List cell
    @Override
    public void onBindViewHolder(final ImprovementViewHolder holder, int position) {
        final Improvement impro = improvements.get(position);
        holder.tvImprovement.setText(impro.getName());
        // This is the background color of an item before any other action was take,
        // the first item will be colored -> color = selected
        holder.cvImprovement.setCardBackgroundColor(setBackgroudColor(impro.isSelected()));

        holder.cvImprovement.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {

                unFocuseText.clearFocus();

                InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                impro.setSelected(!impro.isSelected());
                holder.cvImprovement.setCardBackgroundColor(setBackgroudColor(impro.isSelected()));
            }
        });



    }

    @Override
    public int getItemCount() {
        return improvements == null ? 0 :  improvements.size();
    }

    private int setBackgroudColor(boolean isSelected){
        // Search in each position
        return isSelected ? Color.GREEN : Color.WHITE;
    }


}

package com.example.recipeapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.Models.Step;
import com.example.recipeapp.R;

import java.util.List;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepViewHolder> {

    Context context;
    List<Step> list;
    public InstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @androidx.annotation.NonNull
    @Override
    public InstructionStepViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        return new InstructionStepViewHolder(View.inflate(context, R.layout.list_instructions_steps,null));

    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull InstructionStepViewHolder holder, int position) {
      holder.textView_instructions_step_number.setText(String.valueOf(list.get(position).number));
      holder.textView_instructions_step_title.setText(list.get(position).step);
      holder.recycler_instruction_ingredients.setHasFixedSize(true);
      holder.recycler_instruction_ingredients.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
      InstructionsIngredientsAdapter instructionsIngredientsAdapter=new InstructionsIngredientsAdapter(context,list.get(position).ingredients);
      holder.recycler_instruction_ingredients.setAdapter(instructionsIngredientsAdapter);
      holder.recycler_instruction_equipments.setHasFixedSize(true);
      holder.recycler_instruction_equipments.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
      InstructionsEquipmentsAdapter instructionsEquipmentsAdapter=new InstructionsEquipmentsAdapter(context,list.get(position).equipment);
      holder.recycler_instruction_equipments.setAdapter(instructionsEquipmentsAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionStepViewHolder extends RecyclerView.ViewHolder{
    TextView textView_instructions_step_number,textView_instructions_step_title;
    RecyclerView recycler_instruction_equipments,recycler_instruction_ingredients;
    public InstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instructions_step_number=itemView.findViewById(R.id.textView_instructions_step_number);
        textView_instructions_step_title=itemView.findViewById(R.id.textView_instructions_step_title);
        recycler_instruction_equipments=itemView.findViewById(R.id.recycler_instruction_equipments);
        recycler_instruction_ingredients=itemView.findViewById(R.id.recycler_instruction_ingredients);


    }
}

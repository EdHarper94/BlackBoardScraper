package harps.blackboardscraper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @file ModuleAdapter.java
 * @author Ed Harper
 * @date 28/03/2017
 * @see ModuleScraper
 *
 *
 * Expands module_view into list view
 */

public class ModuleAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<Module> modules = new ArrayList<>();

    public ModuleAdapter(Context context, ArrayList<Module> modules){
        this.context = context;
        this.inflater = (LayoutInflater.from(context));
        this.modules = modules;
    }

    /**
     * Gets the total modules in modules Arrau
     * @return
     */
    @Override
    public int getCount() {
        return modules.size();
    }

    /**
     * Gets the item at the passes position
     * @param position the position in the expanded view
     * @return the module item at the passed position
     */
    @Override
    public Object getItem(int position) {
        return modules.get(position);
    }

    /**
     * Gets id of module at a position
     * @param position the position in the expanded view
     * @return the id of the module at the passes position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Holds the items for recycling
     */
    public class ViewHolder{
        public TextView moduleName;
    }

    /**
     * Inflates the module_view, adds module data and adds it to the list view
     * @param position the items position
     * @param convertView the view to be recycled
     * @param viewGroup parent view
     * @return recycled convertView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        // New view
        if(convertView == null) {
            // Init new view holder
            viewHolder = new ViewHolder();
            // Inflate view
            convertView = inflater.inflate(R.layout.module_view, viewGroup, false);

            // Add to views
            viewHolder.moduleName = (TextView) convertView.findViewById(R.id.module_name_view);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Set data
        viewHolder.moduleName.setText(modules.get(position).getModuleName().toString());

        return convertView;
    }
}

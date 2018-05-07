package middletownmusic.org.midwestradio.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import middletownmusic.org.midwestradio.R;

public class DrawerUtils {

    public static class DrawerItem {
        public String title;
        public int icon;

        DrawerItem(String title, int icon) {
            this.title = title;
            this.icon = icon;
        }
    }

    public static class DrawerAdapter extends BaseAdapter {
        private Context context;
        private static final String[] NAV_TITLES = {"All Stations", "About", "Purple Calves", "Submit a Station", "Report a Station"};
        private static final int[] NAV_ICONS = { R.drawable.radio_icon, R.drawable.setup_icon,  R.drawable.web_icon, R.drawable.web_icon, R.drawable.web_icon};
        private DrawerItem[] drawerItems;
        private int count;
        private DrawerItemClickListener itemClickListener;

        public DrawerAdapter(Context context) {
            this.context = context;
            itemClickListener = (DrawerItemClickListener) context;
            this.drawerItems = new DrawerItem[NAV_TITLES.length];
            this.count = NAV_TITLES.length;
            for(int i = 0; i < NAV_TITLES.length; i++) {
                drawerItems[i] = new DrawerItem(NAV_TITLES[i], NAV_ICONS[i]);
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public DrawerItem getItem(int position) {
            return drawerItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DrawerItem drawerItem = getItem(position);
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.drawer_item, parent, false);
                ViewHolder viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                convertView.setOnClickListener((view) -> itemClickListener.drawerItemClicked(getItem(position)));
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.title.setText(drawerItem.title);
            viewHolder.icon.setImageResource(drawerItem.icon);

            return convertView;
        }

        private class ViewHolder {
            TextView title;
            ImageView icon;

            ViewHolder(View view) {
                title = view.findViewById(R.id.title);
                icon = view.findViewById(R.id.icon);
            }
        }
    }

    public interface DrawerItemClickListener {
        void drawerItemClicked(DrawerItem item);
    }
}

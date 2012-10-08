/*
 * 	CouchTatertot is a android app for managing couchpotato
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/couch-tatertot/
 * 	
 * 	libCouchPotato is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.couchtatertot.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.couchpotato.CouchPotato.PageEnum;
import org.couchpotato.json.MovieJson;
import org.couchtatertot.EditMovieActivity;
import org.couchtatertot.R;
import org.couchtatertot.app.LoadingListFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.widget.LoadingPosterView;
import org.couchtatertot.widget.SafeArrayAdapter;

import java.util.List;

public class WantedFragment extends LoadingListFragment<Void, Void, List<MovieJson>> {
	
	private SafeArrayAdapter<MovieJson> movieAdapter;

	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    movieAdapter = new SafeArrayAdapter<MovieJson>(this.getActivity(), R.layout.wanted_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( row == null ) {
					row = layoutInflater.inflate(R.layout.wanted_banner_item, null);
				}
				MovieJson item = getItem(position);
				TextView title = (TextView) row.findViewById(R.id.titleTextView);
				TextView year = (TextView) row.findViewById(R.id.yearTextView);
				TextView plot = (TextView) row.findViewById(R.id.plotTextView);
				TextView quality = (TextView) row.findViewById(R.id.qualityTextView1);
				LoadingPosterView poster = (LoadingPosterView) row.findViewById(R.id.posterLoadingPosterView);
				title.setText(item.library.titles.get(0).title);
				if ( item.library.year > 0 )
					year.setText(item.library.year + ""); // setText(int) is for resources, dont ever pass an int that you want as a string
				else
					year.setText("");
				plot.setText(item.library.plot);
				if ( item.profile != null)
					quality.setText(item.profile.label);
				poster.setPoster(item.library.getCroppedPoster());
				return row;
			}
		};
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.getListView().setBackgroundResource(R.color.couchpotato_background);
		this.getListView().setCacheColorHint(R.color.couchpotato_background);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent( this.getSherlockActivity(), EditMovieActivity.class );
		MovieJson item = movieAdapter.getItem(position);
		intent.putExtra("id", item.id);
		intent.putExtra("page", PageEnum.WANTED.name());
		intent.putExtra("imdb", item.library.info.imdb);
		startActivity(intent);
	}

	@Override
	protected String getEmptyText() {
		return "No Movies Available";
	}
	
	@Override
	protected Void[] getRefreshParams() {
		return null;
	}
	
	@Override
	protected List<MovieJson> doInBackground(Void... arg0) throws Exception {
		return Preferences.getSingleton(getSherlockActivity()).getCouchPotato().movieList(null, -1, -1, null, null);
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}
	
	@Override
	protected void onPostExecute(List<MovieJson> result) {
		setListAdapter(movieAdapter);
		movieAdapter.clear();
		for ( MovieJson s : result ) {
			movieAdapter.add(s);
		}
		if ( movieAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		} else {
			this.setListStatus(ListStatus.NORMAL);
		}
		movieAdapter.notifyDataSetChanged();
	}
}

/**
* Copyright IBM Corporation 2009-2017
*
* Licensed under the Eclipse Public License - v 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.eclipse.org/legal/epl-v10.html
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
* @Author Doug Wood
**/
package psdi.app.bim.viewer.dataapi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedList;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/*
 * {  
 * 	"items" : 
 * 	[ 
 * 		{    
 * 			"bucketKey" : "test27apfkowtolsxergcabqjvg3obggunhda",    
 * 			"objectKey" : "office_a.rvt",    
 * 			"objectId" : "urn:adsk.objects:os.object:test27apfkowtolsxergcabqjvg3obggunhda/office_a.rvt",    
 * 			"sha1" : "f94fd79af64843cef3b9eff9c89fc3bf8ca43eb8",    
 * 			"size" : 11374592,    
 * 			"location" : "https://developer.api.autodesk.com/oss/v2/buckets/test27apfkowtolsxergcabqjvg3obggunhda/objects/office_a.rvt"  
 * 		} 
 * 	]
 * }
 * 
 * @author Doug
 *
 */
public class ResultObjectList
    extends Result
{
	public static final String	KEY_ITEMS = "items";  
	
	private LinkedList<ViewerObject> _objects;
	
	public ResultObjectList()
	{
		super();
	}

	public ResultObjectList(
	    Result result )
	{
		super( result );
	}

	public ResultObjectList(
	    HttpURLConnection connection 
    ) {
		super( connection );
	}

	public ResultObjectList(
	    Exception e 
    ) {
		super( e );
	}
	
	public int size()
	{
		if( _objects == null ) return 0;
		return _objects.size();
	}
	
	public ViewerObject getObject( int index )
	{
		if( _objects == null ) return null;
		if( index < 0 || index >= _objects.size() ) return null;
		return _objects.get( index );
	}
	
	void append(
		ResultObjectList result
	) {
		if( result == null || result._objects == null )
		{
			return;
		}
		if( _objects == null )
		{
			_objects = result._objects;
			return;
		}
		_objects.addAll( result._objects );
	}
	
    @Override
    protected JSONArtifact parseReturn(
    	String data
	) 
		throws IOException 
	{
    	JSONArtifact jArtifact = super.parseReturn( data );
        if( jArtifact == null ) return null;
    	JSONObject jObj;
    	if( !(jArtifact instanceof JSONObject) )
    	{
    		return null;
    	}
		jObj = (JSONObject)jArtifact;

		Object value = jObj.get( KEY_ITEMS );
	
		if (value != null  && value instanceof  JSONArray )
		{
			JSONArray jArray = (JSONArray)value;
			_objects = new LinkedList<ViewerObject>();
		
			@SuppressWarnings( "rawtypes" )
		    Iterator itr = jArray.listIterator();
			while( itr.hasNext() )
			{
				value = itr.next();
				if( value instanceof JSONObject )
				{
					_objects.add( new ViewerObject( (JSONObject)value ) );
				}
			}
		}

		return jObj;
	}

    @Override
    public String toString()
	{
		if( isError() )
		{
			return super.toString();
		}
		
        StringBuffer buf = new StringBuffer();
        if( _objects != null && _objects.size() > 0 )
        {
        	buf.append( "\n " );
        	Iterator<ViewerObject> itr = _objects.iterator();
        	while( itr.hasNext() )
        	{
            	buf.append( '\t' ).append(  itr.next() ).append( "\n " );
        	}
        }
        
        if( buf.length() == 0 )
        {
        	buf.append( getRawData() );
        }

		return buf.toString();
	}
}

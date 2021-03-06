package csvMerge;

import java.util.HashMap;
import java.util.Map;

public class Record {
  Object id; // some sort of unique identifier
  Map<String, String> values = new HashMap<String,String>(); // all key/values of a single row
  public Record(Object id) {this.id=id;}
  public void put(String key, String value){
    values.put(key, value);
  }
  public String get(String key) {
    return values.get(key);
  }
}
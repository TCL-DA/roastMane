package jakarta.json.bind.adapter;

public interface JsonbAdapter<Original, Adapted> {
   Adapted adaptToJson(Original var1) throws Exception;

   Original adaptFromJson(Adapted var1) throws Exception;
}

package
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.carlosfishingsuppliesims.models.Product

class ProductSpinnerAdapter(context: Context, private val productList: List<Product>) :
    ArrayAdapter<Product>(context, 0, productList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val product = productList[position]

        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = product.name // Assuming "name" is the field holding the product name

        return view
    }
}

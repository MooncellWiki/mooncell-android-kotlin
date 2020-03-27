package wiki.fgo.app.ViewModel

import androidx.lifecycle.ViewModel

class ItemTabViewModel : ViewModel() {

    private var initValue = 1L
    val items = (1..4).map { longToItem(initValue++) }.toMutableList()

    fun getItemById(id: Long): String = items.first { itemToLong(it) == id }
    fun itemId(position: Int): Long = itemToLong(items[position])
    fun contains(itemId: Long): Boolean = items.any { itemToLong(it) == itemId }
    fun addNewItem(position: Int) = items.add(position, longToItem(initValue++))
    fun removeItem(position: Int) = items.removeAt(position)
    val size: Int get() = items.size

    private fun longToItem(item: Long): String = "Tab $item"
    private fun itemToLong(item: String): Long = item.split(" ")[1].toLong()
}
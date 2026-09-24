package com.example.testapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView

data class GalleryItem(val title: String, val imageUrl: String)

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val defaultImages = listOf(
        GalleryItem("Campus Life", "https://picsum.photos/seed/campus/400/400"),
        GalleryItem("Tech Workshop", "https://picsum.photos/seed/tech/400/400"),
        GalleryItem("Library & Labs", "https://picsum.photos/seed/library/400/400")
    )

    private val deviceImages = Array<String?>(3) { null }
    private val urlImages = Array<String?>(3) { null }
    private val customNames = mutableMapOf<Int, String>()
    private val favoritePositions = mutableSetOf<Int>()
    private val selectedPositions = mutableSetOf<Int>()
    private var isMultiSelectMode = false
    private var extraGridBoxesCount = 0

    private var pendingDeviceSlotIndex = -1
    private var adapter: GalleryGridAdapter? = null

    private lateinit var multiSelectBarCard: MaterialCardView
    private lateinit var multiSelectCountText: TextView
    private lateinit var multiSelectBatchOptionsButton: Button
    private lateinit var multiSelectCancelButton: Button

    private val pickDeviceImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (pendingDeviceSlotIndex in 0..2) {
                deviceImages[pendingDeviceSlotIndex] = it.toString()
                adapter?.notifyItemChanged(3 + pendingDeviceSlotIndex)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.galleryToolbar)
        setupOptionsMenu(toolbar)

        multiSelectBarCard = view.findViewById(R.id.multiSelectBarCard)
        multiSelectCountText = view.findViewById(R.id.multiSelectCountText)
        multiSelectBatchOptionsButton = view.findViewById(R.id.multiSelectBatchOptionsButton)
        multiSelectCancelButton = view.findViewById(R.id.multiSelectCancelButton)

        setupMultiSelectBar()

        val recyclerView = view.findViewById<RecyclerView>(R.id.galleryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        adapter = GalleryGridAdapter(
            defaultImages = defaultImages,
            deviceImages = deviceImages,
            urlImages = urlImages,
            customNames = customNames,
            favoritePositions = favoritePositions,
            selectedPositions = selectedPositions,
            isMultiSelectMode = { isMultiSelectMode },
            getExtraBoxesCount = { extraGridBoxesCount },
            onAddDevicePhotoClick = { slotIndex ->
                pendingDeviceSlotIndex = slotIndex
                pickDeviceImageLauncher.launch("image/*")
            },
            onAddUrlPhotoClick = { slotIndex ->
                showUrlInputDialog(slotIndex)
            },
            onItemClick = { position ->
                handleItemClick(position)
            },
            onItemLongClick = { position, cardView ->
                handleItemLongClick(position, cardView)
            },
            onItemRemoveClick = { position ->
                removePhotoAtPosition(position)
            },
            onItemChangeNameClick = { position ->
                showChangeNameDialog(position)
            },
            onItemViewDetailsClick = { position ->
                showImageDetailsDialog(position)
            },
            onItemShareClick = { position ->
                shareImageAtPosition(position)
            },
            onItemFavToggleClick = { position ->
                toggleFavoriteAtPosition(position)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun setupMultiSelectBar() {
        multiSelectCancelButton.setOnClickListener {
            exitMultiSelectMode()
        }

        multiSelectBatchOptionsButton.setOnClickListener { overflowView ->
            val popup = PopupMenu(requireContext(), overflowView, android.view.Gravity.TOP or android.view.Gravity.END)
            popup.menuInflater.inflate(R.menu.menu_gallery_popup, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_picture_info -> {
                        if (selectedPositions.isNotEmpty()) {
                            showImageDetailsDialog(selectedPositions.first())
                        }
                        true
                    }
                    R.id.action_share_photo -> {
                        batchShareSelectedPhotos()
                        true
                    }
                    R.id.action_delete_photo -> {
                        batchDeleteSelectedPhotos()
                        true
                    }
                    R.id.action_fav_photo -> {
                        batchToggleFavoriteSelectedPhotos()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun handleItemClick(position: Int) {
        if (isMultiSelectMode) {
            toggleItemSelection(position)
        } else {
            showImageDetailsDialog(position)
        }
    }

    private fun handleItemLongClick(position: Int, cardView: View) {
        // Play long-press scale transformation animation (shrink to 0.9x then bounce back)
        cardView.animate()
            .scaleX(0.90f)
            .scaleY(0.90f)
            .setDuration(120)
            .withEndAction {
                cardView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(120)
                    .withEndAction {
                        if (!isMultiSelectMode) {
                            enterMultiSelectMode(position)
                        } else {
                            toggleItemSelection(position)
                        }
                    }
                    .start()
            }
            .start()
    }

    private fun enterMultiSelectMode(initialPosition: Int) {
        isMultiSelectMode = true
        selectedPositions.clear()
        selectedPositions.add(initialPosition)
        updateMultiSelectUI()
        adapter?.notifyDataSetChanged()
    }

    private fun exitMultiSelectMode() {
        isMultiSelectMode = false
        selectedPositions.clear()
        updateMultiSelectUI()
        adapter?.notifyDataSetChanged()
    }

    private fun toggleItemSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
            if (selectedPositions.isEmpty()) {
                exitMultiSelectMode()
                return
            }
        } else {
            selectedPositions.add(position)
        }
        updateMultiSelectUI()
        adapter?.notifyItemChanged(position)
    }

    private fun updateMultiSelectUI() {
        if (isMultiSelectMode && selectedPositions.isNotEmpty()) {
            multiSelectBarCard.visibility = View.VISIBLE
            multiSelectCountText.text = "${selectedPositions.size} Selected"
        } else {
            multiSelectBarCard.visibility = View.GONE
        }
    }

    private fun batchShareSelectedPhotos() {
        if (selectedPositions.isEmpty()) return
        val sharedLinks = selectedPositions.mapNotNull { pos ->
            getImageSourceAtPosition(pos)?.let { src -> "${getImageTitleAtPosition(pos)}: $src" }
        }
        if (sharedLinks.isEmpty()) {
            Toast.makeText(requireContext(), "No images loaded in selected slots to share", Toast.LENGTH_SHORT).show()
            return
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Selected Gallery Photos")
            putExtra(Intent.EXTRA_TEXT, "Shared Photos:\n" + sharedLinks.joinToString("\n"))
        }
        startActivity(Intent.createChooser(shareIntent, "Share Selected Images"))
        exitMultiSelectMode()
    }

    private fun batchDeleteSelectedPhotos() {
        if (selectedPositions.isEmpty()) return
        val deletedCount = selectedPositions.size
        for (pos in selectedPositions) {
            when (pos) {
                in 3..5 -> deviceImages[pos - 3] = null
                in 6..8 -> urlImages[pos - 6] = null
            }
            customNames.remove(pos)
        }
        exitMultiSelectMode()
        Toast.makeText(requireContext(), "Deleted/cleared $deletedCount selected photos", Toast.LENGTH_SHORT).show()
    }

    private fun batchToggleFavoriteSelectedPhotos() {
        if (selectedPositions.isEmpty()) return
        val count = selectedPositions.size
        val shouldStar = selectedPositions.any { !favoritePositions.contains(it) }
        for (pos in selectedPositions) {
            if (shouldStar) {
                favoritePositions.add(pos)
            } else {
                favoritePositions.remove(pos)
            }
        }
        val msg = if (shouldStar) "Starred $count selected photos ⭐" else "Un-starred $count selected photos"
        exitMultiSelectMode()
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupOptionsMenu(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_box -> {
                    extraGridBoxesCount++
                    val newPosition = 9 + extraGridBoxesCount - 1
                    adapter?.notifyItemInserted(newPosition)
                    Toast.makeText(requireContext(), "Added extra grid box #${extraGridBoxesCount}", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_remove_box -> {
                    if (extraGridBoxesCount > 0) {
                        extraGridBoxesCount--
                        val removedPosition = 9 + extraGridBoxesCount
                        adapter?.notifyItemRemoved(removedPosition)
                        Toast.makeText(requireContext(), "Removed extra grid box", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "No extra grid boxes to remove", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.action_reset_all -> {
                    for (i in deviceImages.indices) deviceImages[i] = null
                    for (i in urlImages.indices) urlImages[i] = null
                    customNames.clear()
                    favoritePositions.clear()
                    extraGridBoxesCount = 0
                    exitMultiSelectMode()
                    Toast.makeText(requireContext(), "Reset all added images & settings", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_gallery_info -> {
                    val deviceCount = deviceImages.count { it != null }
                    val urlCount = urlImages.count { it != null }
                    val totalItems = 9 + extraGridBoxesCount
                    AlertDialog.Builder(requireContext())
                        .setTitle("Gallery Info")
                        .setMessage("• Default Images: 3\n• Local Device Photos: $deviceCount\n• Web URL Photos: $urlCount\n• Starred Favorites: ${favoritePositions.size}\n• Extra Grid Boxes: $extraGridBoxesCount\n• Total Grid Slots: $totalItems")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun removePhotoAtPosition(position: Int) {
        when (position) {
            in 0..2 -> Toast.makeText(requireContext(), "Default app images cannot be removed", Toast.LENGTH_SHORT).show()
            in 3..5 -> {
                deviceImages[position - 3] = null
                customNames.remove(position)
                adapter?.notifyItemChanged(position)
                Toast.makeText(requireContext(), "Removed photo from slot ${position + 1}", Toast.LENGTH_SHORT).show()
            }
            in 6..8 -> {
                urlImages[position - 6] = null
                customNames.remove(position)
                adapter?.notifyItemChanged(position)
                Toast.makeText(requireContext(), "Removed photo from slot ${position + 1}", Toast.LENGTH_SHORT).show()
            }
            else -> {
                customNames.remove(position)
                adapter?.notifyItemChanged(position)
            }
        }
    }

    private fun showChangeNameDialog(position: Int) {
        val currentName = getImageTitleAtPosition(position)
        val editText = EditText(requireContext()).apply {
            setText(currentName)
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Change Image Name")
            .setMessage("Enter new caption for image #${position + 1}:")
            .setView(editText)
            .setPositiveButton("Save") { dialog, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    customNames[position] = newName
                    adapter?.notifyItemChanged(position)
                    Toast.makeText(requireContext(), "Name updated to '$newName'", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showImageDetailsDialog(position: Int) {
        val name = getImageTitleAtPosition(position)
        val uriStr = getImageSourceAtPosition(position) ?: "No image loaded"
        val isFav = if (favoritePositions.contains(position)) "Yes ⭐" else "No"

        AlertDialog.Builder(requireContext())
            .setTitle("Image Details (#${position + 1})")
            .setMessage("• Name: $name\n• Position: Slot ${position + 1}\n• Starred Favorite: $isFav\n• Source: $uriStr")
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun shareImageAtPosition(position: Int) {
        val imageSource = getImageSourceAtPosition(position)
        if (imageSource == null) {
            Toast.makeText(requireContext(), "No image loaded in this slot to share", Toast.LENGTH_SHORT).show()
            return
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getImageTitleAtPosition(position))
            putExtra(Intent.EXTRA_TEXT, "Check out this image from TestApp: $imageSource")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Image Link"))
    }

    private fun toggleFavoriteAtPosition(position: Int) {
        if (favoritePositions.contains(position)) {
            favoritePositions.remove(position)
            Toast.makeText(requireContext(), "Removed from Favorites ⭐", Toast.LENGTH_SHORT).show()
        } else {
            favoritePositions.add(position)
            Toast.makeText(requireContext(), "Added to Favorites ⭐", Toast.LENGTH_SHORT).show()
        }
        adapter?.notifyItemChanged(position)
    }

    private fun getImageTitleAtPosition(position: Int): String {
        return customNames[position] ?: when (position) {
            in 0..2 -> defaultImages[position].title
            in 3..5 -> "Device Photo #${position - 2}"
            in 6..8 -> "Web Photo #${position - 5}"
            else -> "Grid Box #${position + 1}"
        }
    }

    private fun getImageSourceAtPosition(position: Int): String? {
        return when (position) {
            in 0..2 -> defaultImages[position].imageUrl
            in 3..5 -> deviceImages[position - 3]
            in 6..8 -> urlImages[position - 6]
            else -> null
        }
    }

    private fun showUrlInputDialog(slotIndex: Int) {
        val editText = EditText(requireContext()).apply {
            hint = "https://example.com/image.jpg"
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Image URL")
            .setMessage("Enter a direct link to an image:")
            .setView(editText)
            .setPositiveButton("Add") { dialog, _ ->
                val inputUrl = editText.text.toString().trim()
                if (inputUrl.isNotEmpty()) {
                    urlImages[slotIndex] = inputUrl
                    adapter?.notifyItemChanged(6 + slotIndex)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun scrollToTop() {
        view?.findViewById<RecyclerView>(R.id.galleryRecyclerView)?.smoothScrollToPosition(0)
    }

    private class GalleryGridAdapter(
        private val defaultImages: List<GalleryItem>,
        private val deviceImages: Array<String?>,
        private val urlImages: Array<String?>,
        private val customNames: Map<Int, String>,
        private val favoritePositions: Set<Int>,
        private val selectedPositions: Set<Int>,
        private val isMultiSelectMode: () -> Boolean,
        private val getExtraBoxesCount: () -> Int,
        private val onAddDevicePhotoClick: (Int) -> Unit,
        private val onAddUrlPhotoClick: (Int) -> Unit,
        private val onItemClick: (Int) -> Unit,
        private val onItemLongClick: (Int, View) -> Unit,
        private val onItemRemoveClick: (Int) -> Unit,
        private val onItemChangeNameClick: (Int) -> Unit,
        private val onItemViewDetailsClick: (Int) -> Unit,
        private val onItemShareClick: (Int) -> Unit,
        private val onItemFavToggleClick: (Int) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val TYPE_IMAGE = 0
            private const val TYPE_ADD = 1
        }

        override fun getItemCount(): Int = 9 + getExtraBoxesCount()

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                in 0..2 -> TYPE_IMAGE
                in 3..5 -> if (deviceImages[position - 3] != null) TYPE_IMAGE else TYPE_ADD
                in 6..8 -> if (urlImages[position - 6] != null) TYPE_IMAGE else TYPE_ADD
                else -> TYPE_ADD
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_IMAGE) {
                val view = inflater.inflate(R.layout.item_gallery, parent, false)
                ImageViewHolder(view)
            } else {
                val view = inflater.inflate(R.layout.item_gallery_add, parent, false)
                AddViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ImageViewHolder) {
                val isSelected = selectedPositions.contains(position)
                val isMultiSelect = isMultiSelectMode()

                holder.selectionCheckmark.visibility = if (isSelected) View.VISIBLE else View.GONE
                holder.cardView.strokeWidth = if (isSelected) 6 else 0

                val isFav = favoritePositions.contains(position)
                holder.starIndicator.visibility = if (isFav && !isSelected) View.VISIBLE else View.GONE

                val displayTitle = customNames[position] ?: when (position) {
                    in 0..2 -> defaultImages[position].title
                    in 3..5 -> "Device Photo #${position - 2}"
                    in 6..8 -> "Web Photo #${position - 5}"
                    else -> "Image #${position + 1}"
                }
                holder.captionView.text = displayTitle

                val imageSource = when (position) {
                    in 0..2 -> defaultImages[position].imageUrl
                    in 3..5 -> deviceImages[position - 3]
                    in 6..8 -> urlImages[position - 6]
                    else -> null
                }

                holder.imageView.load(imageSource) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_launcher_round)
                }

                holder.itemView.setOnClickListener {
                    onItemClick(position)
                }

                holder.itemView.setOnLongClickListener {
                    onItemLongClick(position, holder.itemView)
                    true
                }

                // Overflow button on individual card opens individual Context Menu
                holder.overflowButton.setOnClickListener { overflowView ->
                    if (!isMultiSelect) {
                        val popup = PopupMenu(overflowView.context, overflowView, android.view.Gravity.TOP or android.view.Gravity.END)
                        popup.menuInflater.inflate(R.menu.menu_gallery_context, popup.menu)
                        popup.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.action_view_details, R.id.action_picture_info -> {
                                    onItemViewDetailsClick(position)
                                    true
                                }
                                R.id.action_remove_photo -> {
                                    onItemRemoveClick(position)
                                    true
                                }
                                R.id.action_change_name -> {
                                    onItemChangeNameClick(position)
                                    true
                                }
                                else -> false
                            }
                        }
                        popup.show()
                    } else {
                        onItemClick(position)
                    }
                }

                // Context Menu on long-press
                holder.itemView.setOnCreateContextMenuListener { menu, _, _ ->
                    if (!isMultiSelect) {
                        menu.setHeaderTitle("Options: $displayTitle")
                        
                        val detailsItem = menu.add(0, 1, 0, "View Image Details")
                        detailsItem.setOnMenuItemClickListener {
                            onItemViewDetailsClick(position)
                            true
                        }

                        val removeItem = menu.add(0, 2, 1, "Remove Photo")
                        removeItem.setOnMenuItemClickListener {
                            onItemRemoveClick(position)
                            true
                        }

                        val nameItem = menu.add(0, 3, 2, "Change Name")
                        nameItem.setOnMenuItemClickListener {
                            onItemChangeNameClick(position)
                            true
                        }

                        val infoItem = menu.add(0, 4, 3, "Picture Info")
                        infoItem.setOnMenuItemClickListener {
                            onItemViewDetailsClick(position)
                            true
                        }
                    }
                }

            } else if (holder is AddViewHolder) {
                if (position in 3..5) {
                    val slotIndex = position - 3
                    holder.titleView.text = "Add Device Photo"
                    holder.itemView.setOnClickListener {
                        onAddDevicePhotoClick(slotIndex)
                    }
                } else if (position in 6..8) {
                    val slotIndex = position - 6
                    holder.titleView.text = "Add Web URL"
                    holder.itemView.setOnClickListener {
                        onAddUrlPhotoClick(slotIndex)
                    }
                } else {
                    val extraIndex = position - 8
                    holder.titleView.text = "Extra Box #$extraIndex"
                    holder.itemView.setOnClickListener {
                        onAddUrlPhotoClick(0)
                    }
                }
            }
        }

        class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardView: MaterialCardView = view.findViewById(R.id.galleryCard)
            val imageView: ImageView = view.findViewById(R.id.galleryImage)
            val captionView: TextView = view.findViewById(R.id.galleryCaption)
            val starIndicator: TextView = view.findViewById(R.id.starIndicator)
            val selectionCheckmark: TextView = view.findViewById(R.id.selectionCheckmark)
            val overflowButton: ImageView = view.findViewById(R.id.overflowButton)
        }

        class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleView: TextView = view.findViewById(R.id.addTitle)
        }
    }
}

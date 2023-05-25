package me.xra1ny.pluginapi.models.menu;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/** Used to easily create an interactive paged Inventory Menu */
@Slf4j
public abstract class RPagedInventoryMenu extends RInventoryMenu {
    /**
     * the current page of this paged inventory menu
     */
    @Getter
    private int page = 0;

    public RPagedInventoryMenu(@Nullable RInventoryMenu previousMenu) {
        super(previousMenu);
    }

    /**
     * called when the page of this paged inventory menu changes
     * @param page the new page
     */
    protected abstract void onPageChange(int page);

    /**
     * sets the current page of this paged inventory menu
     * @param page the page
     */
    public final void setPage(int page) {
        if(page <= 0 || page == Integer.MAX_VALUE) {
            return;
        }

        this.page = page;

        onPageChange(page);
    }
}

package me.xra1ny.pluginapi.models.menu;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/** Used to easily create an interactive paged Inventory Menu */
@Slf4j
public abstract class RPagedInventoryMenu extends RInventoryMenu {
    @Getter
    private int page = 0;

    public RPagedInventoryMenu(@Nullable RInventoryMenu previousMenu) {
        super(previousMenu);
    }

    protected abstract void onPageChange(int page);

    public final void setPage(int page) {
//        Validate page
        if(page <= 0 || page == Integer.MAX_VALUE) {
            return;
        }

//        Set Page
        this.page = page;
//        Call User defined Method
        onPageChange(page);
    }
}

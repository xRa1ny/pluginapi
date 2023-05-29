package me.xra1ny.pluginapi.models.menu;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.models.user.RUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Used to easily create an interactive paged Inventory Menu */
@Slf4j
public abstract class RPagedInventoryMenu extends RInventoryMenu {
    /**
     * the current page of this paged inventory menu
     */
    @Getter
    private long page = 0;

    public RPagedInventoryMenu(@Nullable RInventoryMenu previousMenu) {
        super(previousMenu);
    }

    /**
     * called when the page of this paged inventory menu changes
     * @param page the new page
     */
    protected abstract void onPageChange(long page, @NotNull RUser user);

    /**
     * sets the current page of this paged inventory menu
     * @param page the page
     */
    public final void setPage(long page, @NotNull RUser user) {
        if(page <= 0) {
            return;
        }

        this.page = page;

        onPageChange(page, user);
    }
}

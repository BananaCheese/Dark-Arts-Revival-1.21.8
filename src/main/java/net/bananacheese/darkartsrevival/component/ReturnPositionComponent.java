package net.bananacheese.darkartsrevival.component;

import net.minecraft.util.math.Vec3d;
import org.ladysnake.cca.api.v3.component.Component;

public interface ReturnPositionComponent extends Component {
    void setPos(double x, double y, double z);
    Vec3d getPos();
    void clear();
    boolean hasPos();
}
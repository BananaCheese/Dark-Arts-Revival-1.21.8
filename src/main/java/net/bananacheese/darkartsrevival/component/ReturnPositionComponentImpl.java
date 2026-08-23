package net.bananacheese.darkartsrevival.component;

import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;

public class ReturnPositionComponentImpl implements ReturnPositionComponent {

    private Vec3d pos;

    @Override
    public void setPos(double x, double y, double z) {
        this.pos = new Vec3d(x, y, z);
    }

    @Override
    public Vec3d getPos() {
        return pos;
    }

    @Override
    public void clear() {
        pos = null;
    }

    @Override
    public boolean hasPos() {
        return pos != null;
    }

    // ✅ CCA 7 SERIALIZATION

    public void writeData(WriteView view) {
        if (pos != null) {
            view.putDouble("x", pos.x);
            view.putDouble("y", pos.y);
            view.putDouble("z", pos.z);
        }
    }

    public void readData(ReadView view) {
        if (view.contains("x")) {
            pos = new Vec3d(
                    view.getDouble("x", 0.0),
                    view.getDouble("y", 0.0),
                    view.getDouble("z", 0.0)
            );
        }
    }
}
package service;

import dao.SlotDAO;
import model.Slot;

import java.util.List;

public class SlotService {

    private final SlotDAO slotDAO;

    public SlotService(SlotDAO slotDAO) {
        this.slotDAO = slotDAO;
    }

    public List<Slot> getAvailableSlots() {
        return slotDAO.getAvailableSlots();
    }

    public List<Slot> getAvailableSlotsByDoctor(int doctorId) {
    return slotDAO.getAvailableSlotsByDoctor(doctorId);
}

    public Slot getSlot(int slotId) {
        return slotDAO.getSlotById(slotId);
    }
}
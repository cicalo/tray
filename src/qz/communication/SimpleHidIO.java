package qz.communication;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import javax.usb.util.UsbUtil;
import java.io.IOException;

public class SimpleHidIO implements DeviceIO {

    private HidDeviceInfo deviceInfo;
    private HidDevice device;

    private boolean streaming;


    public SimpleHidIO(Short vendorId, Short productId) throws DeviceException {
        this(SimpleHidUtilities.findDevice(vendorId, productId));
    }

    public SimpleHidIO(HidDeviceInfo deviceInfo) throws DeviceException {
        if (deviceInfo == null) {
            throw new DeviceException("HID device could not be found");
        }

        this.deviceInfo = deviceInfo;
    }

    public void open() throws DeviceException {
        if (!isOpen()) {
            try {
                device = PureJavaHidApi.openDevice(deviceInfo);
            } catch (IOException ex) {
                throw new DeviceException(ex);
            }
        }
    }

    public boolean isOpen() {
        return device != null;
    }

    public void setStreaming(boolean active) {
        streaming = active;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public String getVendorId() {
        return UsbUtil.toHexString(deviceInfo.getVendorId());
    }

    public String getProductId() {
        return UsbUtil.toHexString(deviceInfo.getProductId());
    }

    public byte[] readData(int responseSize, Byte unused) throws DeviceException {
        byte[] response = new byte[responseSize];

        int read = device.setFeatureReport(response, responseSize);
        if (read == -1) {
            throw new DeviceException("Failed to read from device");
        }

        return response;
    }

    public void sendData(byte[] data, Byte reportId) throws DeviceException {
        if (reportId == null) { reportId = (byte)0x00; }

        int wrote = device.setOutputReport(reportId, data, data.length);
        if (wrote == -1) {
            throw new DeviceException("Failed to write to device");
        }
    }

    public void close() {
        if (isOpen()) {
            device.close();
        }
    }

}

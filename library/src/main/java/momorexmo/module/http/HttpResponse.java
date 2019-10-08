package momorexmo.module.http;

public class HttpResponse
{
    public int HataKodu;
    public String HataAciklama = "", Data= "";
    public boolean isException;

    @Override
    public String toString() {
        return Data;
    }

    public String getMessage()
    {
        return HataKodu < 0 ? "Server Bağlantısı Kurulumadı." : HataAciklama;
    }
}

package com.mikeblackman.bluetoothplayground

import android.bluetooth.BluetoothAdapter
import com.mikeblackman.bluetoothplayground.permissions.CheckRequiredPermissions
import com.mikeblackman.bluetoothplayground.permissions.CheckRequiredPermissionsImpl
import com.mikeblackman.bluetoothplayground.permissions.RequestPermissions
import com.mikeblackman.bluetoothplayground.permissions.RequestPermissionsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BluetoothModule {

    @Provides
    fun provideBluetoothAdapter() : BluetoothAdapter {
        return BluetoothAdapter.getDefaultAdapter()
    }

    @Provides
    fun provideScanDeviceUseCase(scanDeviceImpl: ScanDeviceImpl) : ScanDevice {
        return scanDeviceImpl
    }

    @Provides
    @Singleton
    fun provideBluetoothService(bluetoothService: BluetoothServiceImpl) : BluetoothService {
        return bluetoothService
    }

    @Provides
    fun provideCheckRequiredPermissionsUseCase(checkRequiredPermissionsImpl: CheckRequiredPermissionsImpl) : CheckRequiredPermissions {
        return checkRequiredPermissionsImpl
    }

    @Provides
    fun provideRequestPermissionUseCase(requestPermissionsUseCaseImpl: RequestPermissionsImpl) : RequestPermissions {
        return requestPermissionsUseCaseImpl
    }

}

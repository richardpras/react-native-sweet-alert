/**
 * RNSweetAlert index bridge
 * Updated for React Native 0.71+
 */

import { Platform, NativeModules } from 'react-native';

const LINKING_ERROR =
  `The package 'RNSweetAlert' doesn't seem to be linked. Make sure:\n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go (bare workflow required)\n';

const Native = NativeModules.RNSweetAlert
  ? NativeModules.RNSweetAlert
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const DEFAULT_OPTIONS = {
  title: '',
  subTitle: '',
  confirmButtonTitle: 'OK',
  confirmButtonColor: '#AEDEF4',
  barColor: '',
  otherButtonTitle: '',
  otherButtonColor: '',
  style: 'success',
  cancellable: true,
};

const SweetAlert = {
  showAlertWithOptions: (options = DEFAULT_OPTIONS, callback = () => {}) => {
    if (Platform.OS === 'android' && Native.showAlertWithOptions) {
      Native.showAlertWithOptions(options, callback);
    } else {
      console.warn('SweetAlert not available on this platform.');
    }
  },
  dismissAlert: () => {
    if (Platform.OS === 'android' && Native.hideSweetAlert) {
      Native.hideSweetAlert();
    }
  },
};

export default SweetAlert;

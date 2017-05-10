import json


class Config:

    def __init__(self, d):
        self.init_dict = d

        for a, b in d.items():
            if isinstance(b, (list, tuple)):
                setattr(self, a, [Config(x) if isinstance(x, dict) else x for x in b])
            else:
                setattr(self, a, Config(b) if isinstance(b, dict) else b)


def init_config(config_file):
    with open(config_file) as input:
        config_dict = json.load(input)

    with open(config_dict['engine_config']) as engine_input:
        engine_config_dict = json.load(engine_input)

    config_dict['testing']['events'] = engine_config_dict['algorithms'][0]['params']['eventNames']
    config_dict['testing']['primary_event'] = config_dict['testing']['events'][0]
    config_dict['testing']['non_zero_users_file'] += "." + config_dict['splitting']['version']

    config_dict['splitting']['test_file'] += "." + config_dict['splitting']['version']
    config_dict['splitting']['train_file'] += "." + config_dict['splitting']['version']
    config_dict['splitting']['random_seed'] = config_dict['splitting'].get('random_seed', None)
    config_dict['splitting']['split_event'] = config_dict['splitting'].get('split_event',
                                                                           config_dict['testing']['primary_event'])

    if config_dict['splitting']['type'] not in ['random', 'date']:
        raise Exception('Config error: unknown splitting type "%s", only "random" and "date" are allowed' %
                        config_dict['splitting']['type'])


    return Config(config_dict)
